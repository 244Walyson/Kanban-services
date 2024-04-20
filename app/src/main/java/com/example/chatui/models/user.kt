package com.example.chatui.models

class User {

    var id: String = ""
    var username: String = ""
    var nickname: String = ""
    var email: String = ""
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