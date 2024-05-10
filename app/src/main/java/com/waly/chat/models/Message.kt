package com.waly.chat.models

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("id")
    val id: String,
    @SerializedName("sender")
    val sender: Sender,
    @SerializedName("content")
    val content: String,
    @SerializedName("instant")
    val instant: String
)