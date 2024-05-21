package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class Team {

    @SerializedName("id")
    var id: String = ""
    @SerializedName("roomName")
    var roomName: String = ""
    @SerializedName("imgUrl")
    var imgUrl: String = ""
    @SerializedName("latestMessage")
    var latestMessage: String = ""
    @SerializedName("lastActivity")
    var lastActivity: String = ""

    constructor(id: String, roomName: String, imgUrl: String, latestMessage: String) {
        this.id = id
        this.roomName = roomName
        this.imgUrl = imgUrl
        this.latestMessage = latestMessage
    }

    constructor()
}