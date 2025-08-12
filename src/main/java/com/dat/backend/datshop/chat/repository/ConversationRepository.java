package com.dat.backend.datshop.chat.repository;

import com.dat.backend.datshop.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    Optional<Conversation> findByConversationId(String conversationId);

    Optional<Conversation> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    List<Conversation> findByUser1IdOrUser2Id(Long id, Long id1);
}
