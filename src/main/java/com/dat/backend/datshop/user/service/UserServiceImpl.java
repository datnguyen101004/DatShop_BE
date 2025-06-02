package com.dat.backend.datshop.user.service;

import com.dat.backend.datshop.user.dto.UserResponse;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.mapper.UserMapper;
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserResponse getProfile(Long userId) {
        // Get user by ID
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserResponse(user);
    }
}
