package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class TeamMin {

    @SerializedName("id")
    var id: String = ""
    @SerializedName("name")
    var roomName: String = ""
    @SerializedName("imgUrl")
    var imgUrl: String = ""
    @SerializedName("totalMembers")
    var membersCount: Int = 0
    @SerializedName("latestMessage")
    var latestMessage: String = ""
    @SerializedName("description")
    var description: String = ""
    @SerializedName("githubLink")
    var githubLink: String = ""

    constructor(id: String, roomName: String, imgUrl: String, latestMessage: String, description: String, membersCount: Int, githubLink: String) {
        this.id = id
        this.roomName = roomName
        this.imgUrl = imgUrl
        this.latestMessage = latestMessage
        this.membersCount = membersCount
        this.description = description
        this.githubLink = githubLink
    }

    constructor()
}