package com.kanban.chat.dtos;

import com.kanban.chat.models.entities.ChatRoomEntity;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;

public class ChatDTO {

    private String id;
    private String roomName;
    private String imgUrl;
    private Integer totalMembers;
    private String description;
    private List<MessageDTO> messages;


    public ChatDTO() {
    }

    public ChatDTO(String id, String roomName, String imgUrl, Integer totalMembers, String description, List<MessageDTO> messages) {
        this.id = id;
        this.roomName = roomName;
        this.imgUrl = imgUrl;
        this.description = description;
        this.totalMembers = totalMembers;
        this.messages = messages;
    }

    public ChatDTO(ChatRoomEntity chatRoomEntity) {
        this.id = chatRoomEntity.getId();
        this.roomName = chatRoomEntity.getRoomName();
        this.imgUrl = chatRoomEntity.getImgUrl();
        this.description = chatRoomEntity.getDescription();
        this.totalMembers = chatRoomEntity.getMembers().size();
        this.messages = chatRoomEntity.getMessages().stream().map(MessageDTO::new).toList();
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

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void addMember(MessageDTO messageDTO) {
        this.messages.add(messageDTO);
    }

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
