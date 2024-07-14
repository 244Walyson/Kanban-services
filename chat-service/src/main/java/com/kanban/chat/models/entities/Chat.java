package com.kanban.chat.models.entities;

import com.kanban.chat.models.embedded.UserEmbedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document(collection = "chat")
public class Chat {

  private String id;
  private String roomName;
  private String imgUrl;
  private String description;
  private String latestMessage;
  private Date latestActivity;
  private List<UserEmbedded> members = new ArrayList<>();
}
