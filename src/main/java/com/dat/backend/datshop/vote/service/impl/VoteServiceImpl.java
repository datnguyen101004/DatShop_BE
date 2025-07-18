package com.dat.backend.datshop.vote.service.impl;

import com.dat.backend.datshop.authentication.exception.NotAuthorizedException;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import com.dat.backend.datshop.vote.dto.CreateVote;
import com.dat.backend.datshop.vote.dto.VoteResponse;
import com.dat.backend.datshop.vote.entity.Vote;
import com.dat.backend.datshop.vote.entity.VoteType;
import com.dat.backend.datshop.vote.mapper.VoteMapper;
import com.dat.backend.datshop.vote.repository.VoteRepository;
import com.dat.backend.datshop.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {
    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final VoteMapper voteMapper;

    @Override
    public VoteResponse createVote(CreateVote createVote, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lấy thông tin từ đầu vào
        String voteType = createVote.getVoteType().toUpperCase();
        Long targetId = createVote.getTargetId();
        int voteValue = createVote.getVoteValue();
        String comment = createVote.getComment();

        // Nếu người dùng đã vote cho đối tượng này, cập nhật giá trị vote
        Optional<Vote> existingVote = voteRepository.findExistingVote(user.getId(), targetId, voteType);
        if (existingVote.isPresent()) {
            Vote vote = existingVote.get();
            vote.setVoteValue(voteValue);
            vote.setComment(comment);
            voteRepository.save(vote);
            return voteMapper.toVoteResponse(vote);
        }

        // Nếu chưa có vote, tạo mới
        Vote newVote = Vote.builder()
                .userId(user.getId())
                .targetId(targetId)
                .voteType(VoteType.valueOf(voteType))
                .voteValue(voteValue)
                .comment(comment)
                .build();

        voteRepository.save(newVote);
        return voteMapper.toVoteResponse(newVote);
    }

    @Override
    public List<VoteResponse> getAllVotes(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Vote> votes = voteRepository.findAllByUserId(user.getId());

        return votes.stream().map(voteMapper::toVoteResponse).collect(Collectors.toList());
    }

    @Override
    public String deleteVote(Long voteId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Vote> voteOptional = voteRepository.findById(voteId);

        if (voteOptional.isEmpty()) {
            throw new RuntimeException("Vote not found");
        }

        // Kiểm tra xem người dùng có phải là người đã tạo vote này không
        Vote vote = voteOptional.get();
        if (!vote.getUserId().equals(user.getId())) {
            throw new NotAuthorizedException("You are not authorized to delete this vote");
        }
        return "SUCCESS";
    }
}
