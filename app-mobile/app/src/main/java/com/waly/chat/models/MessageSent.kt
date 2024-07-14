package com.waly.chat.models

class MessageSent {

    var content: String = ""

    constructor(content: String) {
        this.content = content
    }

    override fun toString(): String {
        if(content.contains("\n"))
            return "{ \"content\": \"${content.replace("\n", "\\n")}\" }"
        return "{ \"content\": \"$content\" }"
    }
}