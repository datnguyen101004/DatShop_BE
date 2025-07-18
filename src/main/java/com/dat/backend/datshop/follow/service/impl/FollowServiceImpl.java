package com.dat.backend.datshop.follow.service.impl;

import com.dat.backend.datshop.follow.dto.FollowResponse;
import com.dat.backend.datshop.follow.dto.CreateFollow;
import com.dat.backend.datshop.follow.entity.Follow;
import com.dat.backend.datshop.follow.entity.FollowType;
import com.dat.backend.datshop.follow.mapper.FollowMapper;
import com.dat.backend.datshop.follow.repository.FollowRepository;
import com.dat.backend.datshop.follow.service.FollowService;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final FollowMapper followMapper;

    @Override
    public FollowResponse createFollow(CreateFollow createFollow, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy ra thông tin từ request
        Long targetId = createFollow.getTargetId();
        String followType = createFollow.getFollowType().toUpperCase();

        // Kiểm tra xem người dùng đã follow chưa, nếu đã follow thì hủy
        Optional<Follow> existingFollow = followRepository.findExistFollow(
                user.getId(), targetId, followType);

        if (existingFollow.isPresent()) {
            // Nếu đã follow, xóa theo dõi
            Follow follow = existingFollow.get();
            follow.setDeleted(true); // Đánh dấu là đã xóa
            followRepository.save(follow);
            log.info("User {} has unfollowed target {}", user.getId(), targetId);
            return followMapper.toFollowResponse(follow);
        }

        // Nếu chưa follow, tạo mới
        Follow newFollow = Follow.builder()
                .userId(user.getId())
                .targetId(targetId)
                .followType(FollowType.valueOf(followType))
                .build();
        followRepository.save(newFollow);
        return followMapper.toFollowResponse(newFollow);
    }

    @Override
    public List<FollowResponse> getAllFollows(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Follow> follows = followRepository.findAllByUserIdAndIsDeletedFalse(user.getId());
        if (follows != null && !follows.isEmpty()) {
            return follows.stream().map(followMapper::toFollowResponse).collect(Collectors.toList());
        }
        return List.of();
    }
}
