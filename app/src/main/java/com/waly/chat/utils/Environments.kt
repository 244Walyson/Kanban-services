package com.waly.chat.utils

class Environments {
    companion object {
//        const val BASE_CHAT_URL = "http://10.0.2.2:8081"
//        const val BASE_CHAT_WEBSOCKET_URL = "ws://10.0.2.2:8081"
//        const val BASE_KANBAN_URL = "http://10.0.2.2:8080"
//        const val BASE_NOTIFICATION_URL = "http://10.0.2.2:8082"

        const val BASE_CHAT_URL = "https://waly1-chat-service.api.waly.dev.br" //"http://10.0.2.2:8090" //
        const val BASE_CHAT_WEBSOCKET_URL = "wss://waly1-chat-service.api.waly.dev.br" //"ws://10.0.2.2:8090" //
        const val BASE_KANBAN_URL = "https://waly1-kanban-service.api.waly.dev.br" //"http://10.0.2.2:9090" //
        const val BASE_NOTIFICATION_URL = "https://waly1-notification-service.api.waly.dev.br" //"http://10.0.2.2:9091"

        const val GITHUB_PARAM = "/oauth2/authorization/github"
        const val GOOGLE_PARAM = "/oauth2/authorization/google"
        const val CLIENT_ID = "myclientid"
        const val CLIENT_SECRET = "myclientsecret"
        const val CHANNEL_ID = "com.waly.chat"
        const val CHANNEL_NAME = "ChatUI"
        const val CHANNEL_DESCRIPTION = "ChatUI Notification Channel"

    }
}