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
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountUserRepo userRepository;
    private final UserOAuthProviderRepo oAuthProviderRepo;
    private final GeneralUserRepo generalUserRepo;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("OAuth2 authentication error", ex);
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        // Validate that we got an ID from the provider
        if (!StringUtils.hasText(oAuth2UserInfo.getId())) {
            throw new OAuth2AuthenticationProcessingException("ID not found from OAuth2 provider");
        }

        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        String providerId = oAuth2UserInfo.getId();

        // Check if this OAuth provider is already linked to a user
        Optional<UserOAuthProvider> existingOAuthProvider = oAuthProviderRepo
                .findByProviderAndProviderId(provider, providerId);

        UserAccount user;

        if (existingOAuthProvider.isPresent()) {
            // User exists with this OAuth provider - update their info
            user = existingOAuthProvider.get().getUserAccount();
            user = updateExistingUser(user, oAuth2UserInfo);
            updateOAuthProvider(existingOAuthProvider.get(), oAuth2UserRequest);
        } else {
            // New OAuth login
            String email = oAuth2UserInfo.getEmail();

            if (StringUtils.hasText(email)) {
                // Check if user exists with this email
                Optional<UserAccount> existingUser = userRepository.findByEmail(email);

                if (existingUser.isPresent()) {
                    // Link OAuth provider to existing user
                    user = existingUser.get();
                    user = updateExistingUser(user, oAuth2UserInfo);
                    createOAuthProvider(user, provider, providerId, oAuth2UserRequest);
                } else {
                    // Create new user
                    user = registerNewUser(oAuth2UserInfo, provider);
                    createOAuthProvider(user, provider, providerId, oAuth2UserRequest);
                }
            } else {
                // No email from provider (e.g., Instagram)
                // Create user with providerId as identifier
                user = registerNewUserWithoutEmail(oAuth2UserInfo, provider, providerId);
                createOAuthProvider(user, provider, providerId, oAuth2UserRequest);
            }
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private UserAccount registerNewUser(OAuth2UserInfo oAuth2UserInfo, AuthProvider provider) {
        log.info("Registering new user from OAuth2 provider: {} with email: {}",
                provider, oAuth2UserInfo.getEmail());

        // Create GeneralUser
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
        log.info("Registering new user from OAuth2 provider: {} without email", provider);

        // Create GeneralUser
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
                                     String providerId, OAuth2UserRequest request) {
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

    private void updateOAuthProvider(UserOAuthProvider oAuthProvider, OAuth2UserRequest request) {
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

        // Clean up the name for username
        String cleanName = baseName.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        if (cleanName.isEmpty()) {
            cleanName = "user";
        }

        // Check if username exists and add suffix if needed
        String username = cleanName;
        int suffix = 1;
        while (userRepository.existsByUsername(username)) {
            username = cleanName + "_" + suffix++;
        }

        return username;
    }
}
