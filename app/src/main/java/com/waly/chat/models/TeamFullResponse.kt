package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class TeamFullResponse {

    @SerializedName("content")
    var content: List<TeamMin> = ArrayList()

    constructor(content: List<TeamMin>) {
        this.content = content
    }
    constructor()
}