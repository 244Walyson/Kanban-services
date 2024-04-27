package com.example.chatui.utils

class Environments {
    companion object {
        const val BASE_CHAT_URL = "http://10.0.2.2:8090" //"http://chat.rancher.waly.dev.br"
        const val BASE_CHAT_WEBSOCKET_URL = "ws://10.0.2.2:8090" //"ws://chat.rancher.waly.dev.br"
        const val BASE_KANBAN_URL =  "http://10.0.2.2:9090" //"http://kanban.rancher.waly.dev.br"
        const val GITHUB_PARAM = "/oauth2/authorization/github"
        const val GOOGLE_PARAM = "/oauth2/authorization/google"
        const val CLIENT_ID = "myclientid"
        const val CLIENT_SECRET = "myclientsecret"
        const val GOOGLE_CLIENT_ID = "495915806430-ge0q86rnfb4ubnog1282tlkj52aebfm7.apps.googleusercontent.com"
        const val CHANNEL_ID = "com.example.chatui"
        const val CHANNEL_NAME = "ChatUI"
        const val CHANNEL_DESCRIPTION = "ChatUI Notification Channel"

    }
}