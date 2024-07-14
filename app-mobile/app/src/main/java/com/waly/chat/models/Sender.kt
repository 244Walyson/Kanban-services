package com.waly.chat.models

import com.google.gson.annotations.SerializedName

data class Sender(

    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("nickname")
    val nickName: String,
    @SerializedName("imgUrl")
    val imgUrl: String
)