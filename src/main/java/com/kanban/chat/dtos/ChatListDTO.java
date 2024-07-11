package com.kanban.chat.dtos;

import com.kanban.chat.models.entities.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatListDTO {

  private String id;
  private String roomName;
  private String imgUrl;
  private String latestMessage;
  private Date lastActivity;

  public static ChatListDTO of(Chat chat) {
    return ChatListDTO.builder()
            .id(chat.getId())
            .roomName(chat.getRoomName())
            .imgUrl(chat.getImgUrl())
            .latestMessage(chat.getLatestMessage())
            .lastActivity(chat.getLatestActivity())
            .build();
  }
}
