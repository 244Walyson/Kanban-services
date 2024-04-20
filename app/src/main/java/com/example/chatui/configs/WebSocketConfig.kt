package com.example.chatui.configs

import SessionManager
import android.content.Context
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets

import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.websocket.ktor.KtorWebSocketClient

class WebSocketConfig(private val context: Context) {

    private val sessionManager = SessionManager(context)

    private val BASE_URL = "ws://10.0.2.2:8090/connect"
    private val token = sessionManager.accessToken

    val client = StompClient(KtorWebSocketClient())

    private var httpClient = HttpClient {
        install(WebSockets)
    }

    val wsClient = KtorWebSocketClient(httpClient)
    val stompClient = StompClient(wsClient)

    suspend fun connect(): StompSession {
        val accessToken = token?.split(" ")?.get(1)
        return stompClient.connect(BASE_URL + "?token=$accessToken")
    }
}