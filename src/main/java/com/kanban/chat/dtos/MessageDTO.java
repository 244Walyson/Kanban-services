package com.kanban.chat.dtos;

import com.kanban.chat.models.embedded.UserEmbedded;
import com.kanban.chat.models.entities.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MessageDTO {

    private String id;
    private UserEmbeddedDTO sender;
    private String content;
    private Date sendAt;

    public static MessageDTO of(Message entity) {
        return MessageDTO.builder()
                .id(entity.getId())
                .sender(UserEmbeddedDTO.of(entity.getSender()))
                .content(entity.getContent())
                .sendAt(entity.getInstant())
                .build();
    }
}
