package org.mobilehub.identity_service.mapper;

import org.mapstruct.Mapper;
import org.mobilehub.identity_service.dto.request.CreateUserRequest;
import org.mobilehub.identity_service.dto.request.RegisterUserRequest;
import org.mobilehub.identity_service.dto.response.AdminUserResponse;
import org.mobilehub.identity_service.dto.response.UserInfo;
import org.mobilehub.identity_service.dto.response.UserResponse;
import org.mobilehub.identity_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper{

    User toUser(RegisterUserRequest registerRequest);

    User toUser(CreateUserRequest request);

    UserInfo toUserInfo(User user);

    UserResponse toUserResponse(User user);
    AdminUserResponse toAdminUserResponse(User user);
}