package com.waly.chat.models

import com.google.gson.annotations.SerializedName
import java.util.Date

class Notification {

    @SerializedName("id")
    var id: Long? = null
    @SerializedName("title")
    var title: String? = null
    @SerializedName("sender")
    var sender: User? = null
    @SerializedName("message")
    var message: String? = null
    @SerializedName("createdAt")
    var createdAt: Date? = null
    @SerializedName("status")
    var status: Boolean = false

    constructor(id: Long?, title: String?, sender: User?, message: String?, createdAt: Date?, status: Boolean) {
        this.id = id
        this.title = title
        this.sender = sender
        this.message = message
        this.createdAt = createdAt
        this.status = status
    }

    constructor()

}