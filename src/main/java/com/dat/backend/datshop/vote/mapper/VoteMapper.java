package com.dat.backend.datshop.vote.mapper;

import com.dat.backend.datshop.vote.dto.VoteResponse;
import com.dat.backend.datshop.vote.entity.Vote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VoteMapper {
    VoteResponse toVoteResponse(Vote vote);
}
