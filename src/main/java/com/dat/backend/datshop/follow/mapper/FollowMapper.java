package com.dat.backend.datshop.follow.mapper;

import com.dat.backend.datshop.follow.dto.FollowResponse;
import com.dat.backend.datshop.follow.entity.Follow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FollowMapper {

    FollowResponse toFollowResponse(Follow follow);
}
