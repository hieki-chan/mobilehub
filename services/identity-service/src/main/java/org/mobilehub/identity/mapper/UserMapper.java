package org.mobilehub.identity.mapper;

import org.mapstruct.Mapper;
import org.mobilehub.identity.dto.request.RegisterUserRequest;
import org.mobilehub.identity.dto.response.UserInfo;
import org.mobilehub.identity.dto.response.UserResponse;
import org.mobilehub.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper{

    User toUser(RegisterUserRequest registerRequest);
    UserInfo toUserInfo(User user);

    UserResponse toUserResponse(User user);
}