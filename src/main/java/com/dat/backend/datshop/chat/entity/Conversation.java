package com.dat.backend.datshop.chat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "room")
public class Conversation {
    @Id
    private String conversationId;

    private Long user1Id; // ID của người dùng 1
    private Long user2Id; // ID của người dùng 2

    @OneToMany(mappedBy = "conversation")
    private List<Message> listMessages;
}
