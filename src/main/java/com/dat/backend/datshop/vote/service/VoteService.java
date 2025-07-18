package com.dat.backend.datshop.vote.service;

import com.dat.backend.datshop.vote.dto.CreateVote;
import com.dat.backend.datshop.vote.dto.VoteResponse;

import java.util.List;

public interface VoteService {
    VoteResponse createVote(CreateVote createVote, String name);

    List<VoteResponse> getAllVotes(String name);

    String deleteVote(Long voteId, String name);
}
