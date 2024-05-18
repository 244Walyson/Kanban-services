package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class CreateUser {

    @SerializedName("name")
    var name: String? = null
    @SerializedName("nickname")
    var nickname: String? = null
    @SerializedName("email")
    var email: String? = null
    @SerializedName("bio")
    var bio: String? = null
    @SerializedName("password")
    var password: String? = null

    constructor(name: String, nickname: String, email: String, bio: String, password: String) {
        this.name = name
        this.nickname = nickname
        this.email = email
        this.bio = bio
        this.password = password
    }
    constructor()
}
