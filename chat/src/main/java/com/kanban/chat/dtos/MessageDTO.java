package com.kanban.chat.dtos;

import com.kanban.chat.models.embedded.ChatMessageEmbedded;
import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.ChatMessageEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDTO {

    private String id;
    private UserEmbedded sender;
    private String content;
    private Instant sendAt;

    public MessageDTO(ChatMessageEntity message) {
        this.id = message.getId();
        this.sender = message.getSender();
        this.content = message.getContent();
        this.sendAt = message.getInstant();
    }

    public MessageDTO(ChatMessageEmbedded chatMessageEmbedded) {
        this.id = chatMessageEmbedded.getId();
        this.sender = chatMessageEmbedded.getSender();
        this.content = chatMessageEmbedded.getContent();
        this.sendAt = chatMessageEmbedded.getSendAt();
    }
}
