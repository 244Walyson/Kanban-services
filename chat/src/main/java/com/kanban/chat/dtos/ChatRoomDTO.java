package com.kanban.chat.dtos;

import com.kanban.chat.models.entities.ChatRoomEntity;
import lombok.Data;

public class ChatRoomDTO {

    private String id;
    private String roomName;
    private String imgUrl;
    private String latestMessage;

    public ChatRoomDTO() {
    }

    public ChatRoomDTO(String id, String roomName, String imgUrl, String latestMessage) {
        this.id = id;
        this.roomName = roomName;
        this.imgUrl = imgUrl;
        this.latestMessage = latestMessage;
    }

    public ChatRoomDTO(ChatRoomEntity chatRoomEntity) {
        this.id = chatRoomEntity.getId();
        this.roomName = chatRoomEntity.getRoomName();
        this.imgUrl = chatRoomEntity.getImgUrl();
        this.latestMessage = chatRoomEntity.getMessages().get(chatRoomEntity.getMessages().size() - 1).getContent();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }
}
