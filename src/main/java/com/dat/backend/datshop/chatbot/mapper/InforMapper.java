package com.dat.backend.datshop.chatbot.mapper;

import com.dat.backend.datshop.chatbot.dto.CreateInformation;
import com.dat.backend.datshop.chatbot.dto.InformationResponse;
import com.dat.backend.datshop.chatbot.entity.Information;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InforMapper {
    InformationResponse toInformationResponse(Information information);
}
