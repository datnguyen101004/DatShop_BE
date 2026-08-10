package com.dat.backend.datshop.user.mapper;

import com.dat.backend.datshop.user.dto.UserResponse;
import com.dat.backend.datshop.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")  
public interface UserMapper {
    @Mapping(target = "name", source = "fullName")
    @Mapping(target = "phoneNumber", source = "phone")
    UserResponse toUserResponse(User user);
}
