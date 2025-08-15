package com.dat.backend.datshop.chatbot.service;

import com.dat.backend.datshop.chatbot.dto.CreateInformation;
import com.dat.backend.datshop.chatbot.dto.InformationResponse;
import com.dat.backend.datshop.chatbot.entity.InforType;
import com.dat.backend.datshop.chatbot.entity.Information;
import com.dat.backend.datshop.chatbot.mapper.InforMapper;
import com.dat.backend.datshop.chatbot.repository.InformationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InformationService {
    private final InformationRepository informationRepository;
    private final OllamaService ollamaService;
    private final QdrantService qdrantService;
    private final InforMapper inforMapper;

    public InformationResponse create(CreateInformation createInformation) throws JsonProcessingException {
        Information information = Information.builder()
                .description(createInformation.getDescription())
                .name(createInformation.getName())
                .type(InforType.valueOf(createInformation.getType().toUpperCase())) // Chuyển đổi chuỗi thành enum
                .build();
        information = informationRepository.save(information);

        // Tạo vector embedding thông tin mới
        String textEmbedding = String.format("Type: %s, Name: %s, Description: %s",
                information.getType(), information.getName(), information.getDescription()); // Ex: "Type: Product, Name: Laptop, Description: A high-performance laptop";
        double[] vec = ollamaService.generateEmbedding(textEmbedding).block();

        // sau đó gọi service lưu vec vào Qdrant
        qdrantService.upsertVector(information.getId(), vec);

        return inforMapper.toInformationResponse(information);
    }
}
