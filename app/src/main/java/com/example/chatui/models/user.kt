package com.example.chatui.models

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

    constructor(id: String, username: String, nickname: String, email: String, imgUrl: String) {
        this.id = id
        this.username = username
        this.nickname = nickname
        this.email = email
        this.imgUrl = imgUrl
    }

    constructor()
}