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
import com.dat.backend.datshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    @Override
    public ConversationResponse getOrCreateConservation(Long receiverId, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + name));

        Optional<Conversation> conversationOp = conversationRepository.findByUser1IdAndUser2Id(user.getId(), receiverId);

        if (conversationOp.isPresent()) {
            log.info("Conversation already exists between user {} and receiver {}", user.getId(), receiverId);
            Conversation existingConversation = conversationOp.get();

            List<Message> messages = existingConversation.getListMessages();
            List<MessageResponse> messageResponses = messages.stream()
                    .map(chatMapper::messageToMessageResponse)
                    .toList();

            // Trả về thông tin cuộc trò chuyện
            return ConversationResponse.builder()
                    .conversationId(existingConversation.getConversationId())
                    .user1Id(existingConversation.getUser1Id())
                    .user2Id(existingConversation.getUser2Id())
                    .listMessages(messageResponses)
                    .build();
        }


        // Nếu cuộc trò chuyện chưa tồn tại, tạo mới
        // Tạo ID cuộc trò chuyện duy nhất
        String conversationId = CreateConversationId.createConversationId(user.getId(), receiverId);

        Conversation newConversation = Conversation.builder()
                .conversationId(conversationId)
                .user1Id(user.getId())
                .build();

        conversationRepository.save(newConversation);
        log.info("Created new conversation with id {}", conversationId);
        // Trả về thông tin cuộc trò chuyện mới
        return ConversationResponse.builder()
                .conversationId(newConversation.getConversationId())
                .listMessages(List.of()) // Trả về danh sách tin nhắn rỗng
                .build();
    }

    @Override
    public List<ConversationResponse> getAllConversations(String name) {
    User user = userRepository.findByEmail(name)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + name));

    List<Conversation> conversations = conversationRepository.findByUser1IdOrUser2Id(user.getId(), user.getId());

    return conversations.stream()
            .map(conversation -> {
                List<MessageResponse> messageResponses = conversation.getListMessages().stream()
                        .map(chatMapper::messageToMessageResponse)
                        .toList();

                return ConversationResponse.builder()
                        .conversationId(conversation.getConversationId())
                        .user1Id(conversation.getUser1Id())
                        .user2Id(conversation.getUser2Id())
                        .listMessages(messageResponses)
                        .build();
            })
            .toList();
    }
}
