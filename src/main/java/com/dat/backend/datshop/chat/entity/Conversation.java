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

    @OneToMany(mappedBy = "conversation")
    private List<Message> messageList;
}
