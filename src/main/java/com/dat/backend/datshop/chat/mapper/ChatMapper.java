package com.dat.backend.datshop.chat.mapper;

import com.dat.backend.datshop.chat.dto.MessageResponse;
import com.dat.backend.datshop.chat.entity.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "conversationId", expression = "java(message.getConversation().getConversationId())")
    MessageResponse messageToMessageResponse(com.dat.backend.datshop.chat.entity.Message message);
}
