package com.dat.backend.datshop.chat.service.impl;

import com.dat.backend.datshop.chat.dto.ConversationResponse;
import com.dat.backend.datshop.chat.dto.MessageResponse;
import com.dat.backend.datshop.chat.entity.Conversation;
import com.dat.backend.datshop.chat.entity.Message;
import com.dat.backend.datshop.chat.mapper.ChatMapper;
import com.dat.backend.datshop.chat.repository.ConversationRepository;
import com.dat.backend.datshop.chat.service.ConversationService;
import com.dat.backend.datshop.chat.util.CreateConversationId;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.entity.UserRole;
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    public static final long ADMIN_SUPPORT_ID = 0L;

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    @Override
    @Transactional
    public ConversationResponse getOrCreateConservation(Long receiverId, String email) {
        User requester = getUser(email);
        long requesterId = participantId(requester);
        validateReceiver(requester, receiverId);

        return conversationRepository
                .findFirstByUser1IdAndUser2IdOrUser1IdAndUser2Id(
                        requesterId, receiverId, receiverId, requesterId
                )
                .map(conversation -> toResponse(conversation, requesterId))
                .orElseGet(() -> createConversation(requesterId, receiverId));
    }

    @Override
    public ConversationResponse getOrCreateSupportConversation(String email) {
        return getOrCreateConservation(ADMIN_SUPPORT_ID, email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getAllConversations(String email) {
        User requester = getUser(email);
        long requesterId = participantId(requester);

        return conversationRepository.findByUser1IdOrUser2Id(requesterId, requesterId).stream()
                .filter(this::isSupportConversation)
                .map(conversation -> toResponse(conversation, requesterId))
                .sorted(Comparator.comparing(
                        ConversationResponse::getLastMessageAt,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ).reversed())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponse> getSupportInbox(String email) {
        User requester = getUser(email);
        if (requester.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Only administrators can view the support inbox");
        }
        return getAllConversations(email);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponse getConversation(String conversationId, String email) {
        User requester = getUser(email);
        long requesterId = participantId(requester);
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        if (!isParticipant(conversation, requesterId) || !isSupportConversation(conversation)) {
            throw new RuntimeException("Access denied to this conversation");
        }
        return toResponse(conversation, requesterId);
    }

    private ConversationResponse createConversation(long requesterId, long receiverId) {
        Conversation conversation = Conversation.builder()
                .conversationId(CreateConversationId.createConversationId(requesterId, receiverId))
                .user1Id(requesterId)
                .user2Id(receiverId)
                .build();
        conversationRepository.save(conversation);
        return toResponse(conversation, requesterId);
    }

    private ConversationResponse toResponse(Conversation conversation, long viewerId) {
        List<MessageResponse> messages = conversation.getListMessages() == null
                ? List.of()
                : conversation.getListMessages().stream().map(chatMapper::messageToMessageResponse).toList();
        Long otherUserId = conversation.getUser1Id().equals(viewerId)
                ? conversation.getUser2Id()
                : conversation.getUser1Id();
        User otherUser = otherUserId.equals(ADMIN_SUPPORT_ID)
                ? null
                : userRepository.findById(otherUserId).orElse(null);
        LocalDateTime lastMessageAt = messages.isEmpty() ? null : messages.getLast().getSentAt();

        return ConversationResponse.builder()
                .conversationId(conversation.getConversationId())
                .user1Id(conversation.getUser1Id())
                .user2Id(conversation.getUser2Id())
                .otherUserId(otherUserId)
                .otherUserName(otherUser == null ? "DatShop support" : otherUser.getFullName())
                .otherUserAvatarUrl(otherUser == null ? null : otherUser.getAvatarUrl())
                .lastMessageAt(lastMessageAt)
                .listMessages(messages)
                .build();
    }

    private void validateReceiver(User requester, Long receiverId) {
        if (receiverId == null) {
            throw new RuntimeException("Receiver is required");
        }
        if (requester.getRole() == UserRole.ADMIN) {
            if (receiverId.equals(ADMIN_SUPPORT_ID)) {
                throw new RuntimeException("Administrator must select a customer");
            }
            userRepository.findById(receiverId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            return;
        }
        if (!receiverId.equals(ADMIN_SUPPORT_ID)) {
            throw new RuntimeException("Customers can only message DatShop support");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private long participantId(User user) {
        return user.getRole() == UserRole.ADMIN ? ADMIN_SUPPORT_ID : user.getId();
    }

    private boolean isParticipant(Conversation conversation, long userId) {
        return conversation.getUser1Id().equals(userId) || conversation.getUser2Id().equals(userId);
    }

    private boolean isSupportConversation(Conversation conversation) {
        return conversation.getUser1Id().equals(ADMIN_SUPPORT_ID)
                || conversation.getUser2Id().equals(ADMIN_SUPPORT_ID);
    }
}
