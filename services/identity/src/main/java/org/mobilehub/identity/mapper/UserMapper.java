package org.mobilehub.identity.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mobilehub.identity.dto.request.RegisterUserRequest;
import org.mobilehub.identity.dto.response.UserResponse;
import org.mobilehub.identity.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper{

    User toUser(RegisterUserRequest registerRequest);

    UserResponse toUserResponse(User user);
}