package com.waly.chat.views

import NetworkUtils
import SessionManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.waly.chat.R
import com.waly.chat.databinding.ActivityNotificationBinding
import com.waly.chat.models.Notification
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        listNotifications()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.notificationList.removeAllViews()
            listNotifications()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun listNotifications() {
        val service = NetworkUtils.createServiceNotification()
        val token = session.accessToken

        service.getNotifications(token!!)
            .enqueue(object : Callback<List<Notification>> {
                override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                    if (response.isSuccessful) {
                        val notifications = response.body()
                        showCard(notifications?: emptyList())
                    }
                }

                override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }

    private fun showCard(notifications: List<Notification>) {
        val notificationList = binding.notificationList
        notifications.forEach {notification ->
            val card = layoutInflater.inflate(R.layout.card_notification_item, notificationList, false)
            card.findViewById<TextView>(R.id.notificationTitle).text = notification.title
            card.findViewById<TextView>(R.id.notificationDesc).text = notification.message

            Glide
                .with(this)
                .load(notification.sender?.imgUrl)
                .centerCrop()
                .into(card.findViewById<ImageView>(R.id.notificationImage))

            val notificationIcon = card.findViewById<ImageView>(R.id.notificationIcon)
            if(notification.title!!.contains("Novo pedido de"))  {
                notificationIcon.setOnClickListener {
                    acceptConnection(notification.sender?.id.toString(), card, notificationList)
                }
            } else notificationIcon.visibility = View.GONE
            notificationList.addView(card)
        }
    }

    private fun acceptConnection(userId: String, card: View, notificationList: LinearLayout) {
        val service = NetworkUtils.createServiceUser()
        val token = session.accessToken

        service.acceptConnection(token!!, userId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        card.findViewById<ImageView>(R.id.notificationIcon).visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    t.printStackTrace()
                }
            })
    }
}