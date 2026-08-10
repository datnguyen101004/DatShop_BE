package com.dat.backend.datshop.chat.service.impl;

import com.dat.backend.datshop.chat.dto.MessageResponse;
import com.dat.backend.datshop.chat.dto.SendMessage;
import com.dat.backend.datshop.chat.entity.Conversation;
import com.dat.backend.datshop.chat.entity.Message;
import com.dat.backend.datshop.chat.mapper.ChatMapper;
import com.dat.backend.datshop.chat.repository.ConversationRepository;
import com.dat.backend.datshop.chat.repository.MessageRepository;
import com.dat.backend.datshop.chat.service.ChatService;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.entity.UserRole;
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private static final long ADMIN_SUPPORT_ID = 0L;
    private static final int MAX_MESSAGE_LENGTH = 2000;

    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessage request, String conversationId, String senderEmail) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + senderEmail));
        Conversation conversation = conversationRepository.findByConversationId(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        long senderId = sender.getRole() == UserRole.ADMIN ? ADMIN_SUPPORT_ID : sender.getId();

        validateMessage(request);
        validateParticipants(conversation, sender, senderId, request.getReceiverId());

        Message message = Message.builder()
                .conversation(conversation)
                .senderId(senderId)
                .isRead(false)
                .message(request.getMessage().trim())
                .build();
        return chatMapper.messageToMessageResponse(messageRepository.saveAndFlush(message));
    }

    private void validateMessage(SendMessage request) {
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            throw new RuntimeException("Message cannot be empty");
        }
        if (request.getMessage().trim().length() > MAX_MESSAGE_LENGTH) {
            throw new RuntimeException("Message cannot exceed " + MAX_MESSAGE_LENGTH + " characters");
        }
    }

    private void validateParticipants(Conversation conversation, User sender, long senderId, Long receiverId) {
        boolean senderInRoom = conversation.getUser1Id().equals(senderId)
                || conversation.getUser2Id().equals(senderId);
        if (!senderInRoom) {
            throw new RuntimeException("Access denied to this conversation");
        }

        Long expectedReceiver = conversation.getUser1Id().equals(senderId)
                ? conversation.getUser2Id()
                : conversation.getUser1Id();
        if (receiverId == null || !receiverId.equals(expectedReceiver)) {
            throw new RuntimeException("Invalid message receiver");
        }

        boolean supportRoom = conversation.getUser1Id().equals(ADMIN_SUPPORT_ID)
                || conversation.getUser2Id().equals(ADMIN_SUPPORT_ID);
        if (!supportRoom || (sender.getRole() != UserRole.ADMIN && !receiverId.equals(ADMIN_SUPPORT_ID))) {
            throw new RuntimeException("Customers can only message DatShop support");
        }
    }
}
