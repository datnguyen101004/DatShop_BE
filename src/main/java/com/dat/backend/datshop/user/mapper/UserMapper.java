package com.dat.backend.datshop.user.mapper;

import com.dat.backend.datshop.user.dto.UserResponse;
import com.dat.backend.datshop.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")  
public interface UserMapper {
    UserResponse toUserResponse(User user);
}
