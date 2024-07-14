package com.waly.chat.services

import NetworkUtils
import SessionManager
import android.util.Log
import com.waly.chat.models.FcmToken
import com.waly.chat.notification.MessageNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PushNotificationService: FirebaseMessagingService() {

    private val session: SessionManager by lazy {
        SessionManager(applicationContext)
    }

    override fun onNewToken(token: String) {
        session.tokenSaved = false
        saveToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {

        val notify = MessageNotification(this)
        Log.i("PushNotificationService", "onMessageReceived: ${message.notification?.title} - ${message.notification?.body}")
        notify.showNotification(message.notification!!.title!!, message.notification!!.body!!)
    }


    private fun saveToken(token: String) {
        val service = NetworkUtils.createServiceSaveToken()
        val tokenToSave: FcmToken = FcmToken(token)
        val accessToken = session.accessToken ?: ""

        if(accessToken.isEmpty()) return@saveToken

        service.saveToken(tokenToSave, accessToken)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        session.tokenSaved = true
                        Log.i("PushNotificationService", "Token saved")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.i("PushNotificationService", t.message.toString())
                }
            })
    }

}