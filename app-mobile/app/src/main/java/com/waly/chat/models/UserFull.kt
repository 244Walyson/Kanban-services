package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class UserFull {


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
    @SerializedName("cards")
    var cards: List<Card> = ArrayList<Card>()
    @SerializedName("teams")
    var teams: List<TeamMin> = ArrayList<TeamMin>()

    constructor(id: String, username: String, nickname: String, email: String, imgUrl: String, cards: List<Card>, teams: List<TeamMin>) {
        this.id = id
        this.username = username
        this.nickname = nickname
        this.email = email
        this.imgUrl = imgUrl
        this.cards = cards
        this.teams = teams
    }

    constructor()
}