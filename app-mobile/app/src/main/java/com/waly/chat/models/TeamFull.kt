package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class TeamFull {

    @SerializedName("id")
    var id: String = ""
    @SerializedName("roomName")
    var roomName: String = ""
    @SerializedName("imgUrl")
    var imgUrl: String = ""
    @SerializedName("totalMembers")
    var membersCount: Int = 0
    @SerializedName("latestMessage")
    var latestMessage: String = ""
    @SerializedName("description")
    var description: String = ""
    @SerializedName("messages")
    var messages: List<Message> = ArrayList()

    constructor(id: String, roomName: String, imgUrl: String, latestMessage: String, messages: List<Message>, description: String, membersCount: Int) {
        this.id = id
        this.roomName = roomName
        this.imgUrl = imgUrl
        this.latestMessage = latestMessage
        this.messages = messages
        this.membersCount = membersCount
        this.description = description
    }

    constructor()
}