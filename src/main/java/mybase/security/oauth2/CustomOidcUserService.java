package mybase.security.oauth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mybase.domain.jpa.GeneralUser;
import mybase.domain.jpa.UserAccount;
import mybase.domain.jpa.UserOAuthProvider;
import mybase.domain.types.AuthProvider;
import mybase.domain.types.UserRole;
import mybase.repo.AccountUserRepo;
import mybase.repo.GeneralUserRepo;
import mybase.repo.UserOAuthProviderRepo;
import mybase.security.UserPrincipal;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOidcUserService extends OidcUserService {

    private final AccountUserRepo userRepository;
    private final UserOAuthProviderRepo oAuthProviderRepo;
    private final GeneralUserRepo generalUserRepo;

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("OIDC authentication error", ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        log.info("OIDC attributes from {}: {}", registrationId, oidcUser.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oidcUser.getAttributes()
        );

        log.info("Parsed OAuth2UserInfo - name: {}, email: {}, imageUrl: {}",
                oAuth2UserInfo.getName(), oAuth2UserInfo.getEmail(), oAuth2UserInfo.getImageUrl());

        if (!StringUtils.hasText(oAuth2UserInfo.getId())) {
            throw new OAuth2AuthenticationProcessingException("ID not found from OAuth2 provider");
        }

        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        String providerId = oAuth2UserInfo.getId();

        Optional<UserOAuthProvider> existingOAuthProvider = oAuthProviderRepo
                .findByProviderAndProviderId(provider, providerId);

        UserAccount user;

        if (existingOAuthProvider.isPresent()) {
            user = existingOAuthProvider.get().getUserAccount();
            user = updateExistingUser(user, oAuth2UserInfo);
            updateOAuthProvider(existingOAuthProvider.get(), userRequest);
        } else {
            String email = oAuth2UserInfo.getEmail();

            if (StringUtils.hasText(email)) {
                Optional<UserAccount> existingUser = userRepository.findByEmail(email);

                if (existingUser.isPresent()) {
                    user = existingUser.get();
                    user = updateExistingUser(user, oAuth2UserInfo);
                    createOAuthProvider(user, provider, providerId, userRequest);
                } else {
                    user = registerNewUser(oAuth2UserInfo, provider);
                    createOAuthProvider(user, provider, providerId, userRequest);
                }
            } else {
                user = registerNewUserWithoutEmail(oAuth2UserInfo, provider, providerId);
                createOAuthProvider(user, provider, providerId, userRequest);
            }
        }

        return UserPrincipal.create(user, oidcUser.getAttributes(),
                oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    private UserAccount registerNewUser(OAuth2UserInfo oAuth2UserInfo, AuthProvider provider) {
        log.info("Registering new user from OIDC provider: {} with email: {}",
                provider, oAuth2UserInfo.getEmail());

        GeneralUser generalUser = new GeneralUser();
        generalUserRepo.save(generalUser);

        UserAccount user = UserAccount.builder()
                .email(oAuth2UserInfo.getEmail())
                .name(oAuth2UserInfo.getName())
                .username(generateUsername(oAuth2UserInfo))
                .avatarUrl(oAuth2UserInfo.getImageUrl())
                .primaryProvider(provider)
                .roles(Collections.singleton(UserRole.USER))
                .isEnabled(true)
                .generalUser(generalUser)
                .build();

        return userRepository.save(user);
    }

    private UserAccount registerNewUserWithoutEmail(OAuth2UserInfo oAuth2UserInfo,
                                                     AuthProvider provider,
                                                     String providerId) {
        log.info("Registering new user from OIDC provider: {} without email", provider);

        GeneralUser generalUser = new GeneralUser();
        generalUserRepo.save(generalUser);

        String generatedEmail = provider.name().toLowerCase() + "_" + providerId + "@oauth.local";

        UserAccount user = UserAccount.builder()
                .email(generatedEmail)
                .name(oAuth2UserInfo.getName())
                .username(generateUsername(oAuth2UserInfo))
                .avatarUrl(oAuth2UserInfo.getImageUrl())
                .primaryProvider(provider)
                .roles(Collections.singleton(UserRole.USER))
                .isEnabled(true)
                .generalUser(generalUser)
                .build();

        return userRepository.save(user);
    }

    private UserAccount updateExistingUser(UserAccount existingUser, OAuth2UserInfo oAuth2UserInfo) {
        log.info("Updating existing user: {}", existingUser.getEmail());

        if (StringUtils.hasText(oAuth2UserInfo.getName())) {
            existingUser.setName(oAuth2UserInfo.getName());
        }
        if (StringUtils.hasText(oAuth2UserInfo.getImageUrl())) {
            existingUser.setAvatarUrl(oAuth2UserInfo.getImageUrl());
        }

        return userRepository.save(existingUser);
    }

    private void createOAuthProvider(UserAccount user, AuthProvider provider,
                                     String providerId, OidcUserRequest request) {
        UserOAuthProvider oAuthProvider = UserOAuthProvider.builder()
                .userAccount(user)
                .provider(provider)
                .providerId(providerId)
                .accessToken(request.getAccessToken().getTokenValue())
                .tokenExpiresAt(request.getAccessToken().getExpiresAt() != null
                        ? LocalDateTime.ofInstant(request.getAccessToken().getExpiresAt(),
                                java.time.ZoneId.systemDefault())
                        : null)
                .build();

        oAuthProviderRepo.save(oAuthProvider);
        log.info("Created OAuth provider link for user: {} with provider: {}", user.getEmail(), provider);
    }

    private void updateOAuthProvider(UserOAuthProvider oAuthProvider, OidcUserRequest request) {
        oAuthProvider.setAccessToken(request.getAccessToken().getTokenValue());
        if (request.getAccessToken().getExpiresAt() != null) {
            oAuthProvider.setTokenExpiresAt(
                    LocalDateTime.ofInstant(request.getAccessToken().getExpiresAt(),
                            java.time.ZoneId.systemDefault())
            );
        }
        oAuthProviderRepo.save(oAuthProvider);
    }

    private String generateUsername(OAuth2UserInfo oAuth2UserInfo) {
        String baseName = oAuth2UserInfo.getName();
        if (!StringUtils.hasText(baseName)) {
            baseName = "user";
        }

        String cleanName = baseName.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        if (cleanName.isEmpty()) {
            cleanName = "user";
        }

        String username = cleanName;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = cleanName + "_" + suffix++;
        }

        return username;
    }
}
