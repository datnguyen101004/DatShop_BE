package com.dat.backend.datshop.follow.service;

import com.dat.backend.datshop.follow.dto.FollowResponse;
import com.dat.backend.datshop.follow.dto.CreateFollow;

import java.util.List;

public interface FollowService {
    FollowResponse createFollow(CreateFollow createFollow, String email);

    List<FollowResponse> getAllFollows(String name);
}
