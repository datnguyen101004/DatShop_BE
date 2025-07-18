package com.dat.backend.datshop.follow.repository;

import com.dat.backend.datshop.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    @Query(
            value = "select * from follow f where f.user_id = ?1 and f.target_id = ?2 and f.follow_type = ?3",
            nativeQuery = true
    )
    Optional<Follow> findExistFollow(Long userId, Long targetId, String followType);

    List<Follow> findAllByUserIdAndIsDeletedFalse(Long id);
}
