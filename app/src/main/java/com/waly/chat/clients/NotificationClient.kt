
import com.waly.chat.models.Notification
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface NotificationClient {

    @GET("/notifications")
    fun getNotifications(@Header("Authorization") authorization: String): Call<List<Notification>>

}