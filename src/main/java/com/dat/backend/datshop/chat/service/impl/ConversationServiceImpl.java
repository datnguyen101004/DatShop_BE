package com.dat.backend.datshop.chat.service.impl;

import com.dat.backend.datshop.chat.dto.ConversationResponse;
import com.dat.backend.datshop.chat.dto.MessageResponse;
import com.dat.backend.datshop.chat.entity.Conversation;
import com.dat.backend.datshop.chat.entity.Message;
import com.dat.backend.datshop.chat.mapper.MessageMapper;
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
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Override
    public ConversationResponse getOrCreateConservation(Long receiverId, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + name));

        // Tạo id cuộc trò chuyện
        String conversationId = CreateConversationId.createConversationId(user.getId(), receiverId);

        // Kiểm tra xem cuộc trò chuyện đã tồn tại chưa
        Optional<Conversation> conversationOp = conversationRepository.findByConversationId(conversationId);
        if (conversationOp.isPresent()) {
            log.info("Conversation with id {} already exists", conversationId);
            Conversation existingConversation = conversationOp.get();

            List<Message> messages = existingConversation.getMessageList();
            List<MessageResponse> messageResponses = messages.stream()
                    .map(messageMapper::messageToMessageResponse)
                    .toList();

            // Trả về thông tin cuộc trò chuyện
            return ConversationResponse.builder()
                    .conversationId(existingConversation.getConversationId())
                    .listMessages(messageResponses)
                    .build();
        }

        // Nếu cuộc trò chuyện chưa tồn tại, tạo mới
        Conversation newConversation = Conversation.builder()
                .conversationId(conversationId)
                .build();

        conversationRepository.save(newConversation);
        log.info("Created new conversation with id {}", conversationId);
        // Trả về thông tin cuộc trò chuyện mới
        return ConversationResponse.builder()
                .conversationId(newConversation.getConversationId())
                .listMessages(List.of()) // Trả về danh sách tin nhắn rỗng
                .build();
    }
}
