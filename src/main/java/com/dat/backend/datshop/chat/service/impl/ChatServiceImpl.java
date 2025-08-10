package com.dat.backend.datshop.chat.service.impl;

import com.dat.backend.datshop.chat.dto.SendMessage;
import com.dat.backend.datshop.chat.entity.Conversation;
import com.dat.backend.datshop.chat.entity.Message;
import com.dat.backend.datshop.chat.repository.ConversationRepository;
import com.dat.backend.datshop.chat.repository.MessageRepository;
import com.dat.backend.datshop.chat.service.ChatService;
import com.dat.backend.datshop.user.entity.User;
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Override
    public String sendMessage(SendMessage sendMessageRequest, String conversationId, String senderEmail) {
        User user = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + senderEmail));

        Optional<Conversation> conversationOp = conversationRepository.findByConversationId(conversationId);

        // Kiểm tra xem cuộc trò chuyện đã tồn tại chưa
        if (conversationOp.isPresent()) {
            Conversation conversation = conversationOp.get();

            // Tạo tin nhắn mới
            Message message = Message.builder()
                    .conversation(conversation)
                    .receiverId(sendMessageRequest.getReceiverId())
                    .senderId(user.getId())
                    .message(sendMessageRequest.getMessage())
                    .build();

            messageRepository.save(message);
            return sendMessageRequest.getMessage();
        }

        throw new RuntimeException("Conversation not found");
    }
}
