package com.waly.chat.clients

import com.waly.chat.models.FcmToken
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SaveFcmTokenClient {

    @POST("/users/user/token")
    fun saveToken(@Body token: FcmToken, @Header("Authorization") authorization: String): Call<Void>
}