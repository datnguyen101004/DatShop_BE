package com.dat.backend.datshop.chat.mapper;

import com.dat.backend.datshop.chat.dto.MessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "conversationId", expression = "java(message.getConversation().getConversationId())")
    MessageResponse messageToMessageResponse(com.dat.backend.datshop.chat.entity.Message message);
}
