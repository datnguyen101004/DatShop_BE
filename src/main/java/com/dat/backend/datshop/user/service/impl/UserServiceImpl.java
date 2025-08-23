package com.dat.backend.datshop.user.service.impl;

import com.dat.backend.datshop.user.dto.UserResponse;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.mapper.UserMapper;
import com.dat.backend.datshop.user.repository.UserRepository;
import com.dat.backend.datshop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserResponse getProfile(String email) {
        // Get user by ID
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserProfile(Long id, String name) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id + " and name: " + name));

        return userMapper.toUserResponse(user);
    }
}
