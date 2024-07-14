package com.kanban.chat.models.entities;

import com.kanban.chat.models.embedded.UserEmbedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "message")
public class Message {

    @Id
    private String id;
    private UserEmbedded sender;
    private String content;
    private Date instant;
    private MessageStatus status;
    @DBRef
    private Chat chat;
}
