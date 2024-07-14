package com.waly.chat.models

import com.google.gson.annotations.SerializedName

class UriDTO {

    @SerializedName("uri")
    var uri: String? = null

    constructor(uri: String) {
        this.uri = uri
    }
    constructor()
}