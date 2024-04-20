package com.example.chatui.services

import com.example.chatui.notification.MessageNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushNotificationService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {

        val notify = MessageNotification(this)
        notify.showNotification(message.notification!!.title!!, message.notification!!.body!!)
    }


}