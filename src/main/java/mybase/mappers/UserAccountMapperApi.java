package mybase.mappers;

import mybase.domain.UserAccount;
import mybase.domain.dto.UserAccountDto;

public interface UserAccountMapperApi {


    UserAccountDto mapUserAccountEntityToDto(UserAccount userAccount);


}
