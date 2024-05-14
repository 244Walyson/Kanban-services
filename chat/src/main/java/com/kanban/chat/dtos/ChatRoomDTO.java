package com.kanban.chat.dtos;

import com.kanban.chat.models.entities.ChatRoomEntity;
import com.kanban.chat.models.entities.ChatUserRoomEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.aggregation.DateOperators;

import java.time.Instant;
import java.util.Date;

public class ChatRoomDTO {

    private String id;
    private String roomName;
    private String imgUrl;
    private String latestMessage;
    private Date lastActivity;


    public ChatRoomDTO() {
    }

    public ChatRoomDTO(String id, String roomName, String imgUrl, String latestMessage, Date lastActivity) {
        this.id = id;
        this.roomName = roomName;
        this.imgUrl = imgUrl;
        this.lastActivity = lastActivity;
        this.latestMessage = latestMessage;
    }

    public ChatRoomDTO(ChatRoomEntity chatRoomEntity) {
        this.id = chatRoomEntity.getId();
        this.roomName = chatRoomEntity.getRoomName();
        this.imgUrl = chatRoomEntity.getImgUrl();
        if(chatRoomEntity.getMessages().size() > 0){
            this.latestMessage = chatRoomEntity.getMessages().get(chatRoomEntity.getMessages().size() - 1).getContent();
            this.lastActivity = chatRoomEntity.getMessages().get(chatRoomEntity.getMessages().size() - 1).getSendAt();
            return;
        }
        this.latestMessage = "";
        this.lastActivity = new Date().from(Instant.EPOCH);
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

    public Date getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }
}
