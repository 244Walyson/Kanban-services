package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class User {

    @SerializedName("id")
    var id: String = ""
    @SerializedName("name")
    var username: String = ""
    @SerializedName("nickname")
    var nickname: String = ""
    @SerializedName("email")
    var email: String = ""
    @SerializedName("imgUrl")
    var imgUrl: String = ""
    @SerializedName("connected")
    var isConnected: Boolean = false
    @SerializedName("connectionId")
    var connectionId: String = ""

    constructor(id: String, username: String, nickname: String, email: String, imgUrl: String, isConnected: Boolean, connectionId: String) {
        this.id = id
        this.username = username
        this.nickname = nickname
        this.email = email
        this.imgUrl = imgUrl
        this.isConnected = isConnected
        this.connectionId = connectionId
    }

    constructor()
}