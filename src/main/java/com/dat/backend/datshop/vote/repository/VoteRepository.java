package com.dat.backend.datshop.vote.repository;

import com.dat.backend.datshop.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query(
            value = "select * from vote v where v.user_id = ?1 and v.target_id = ?2 and v.vote_type = ?3",
            nativeQuery = true
    )
    Optional<Vote> findExistingVote(Long userId, Long targetId, String voteType);

    List<Vote> findAllByUserId(Long id);
}
