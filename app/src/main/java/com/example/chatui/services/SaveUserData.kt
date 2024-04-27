package com.example.chatui.services

import SessionManager
import android.content.Context
import android.util.Log
import com.example.chatui.models.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class SaveUserData(context: Context) {

    val session = SessionManager(context)

    fun saveLogedUser() {
        val service = NetworkUtils.createServiceUser()
        val accessToken = session.accessToken

        if (accessToken != null) {
            service.getUser(accessToken)
                .enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            val user = response.body()
                            session.userLogged = user?.nickname.toString()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Log.i("LOGIN", t.message.toString())
                    }
                })
        }
    }
}