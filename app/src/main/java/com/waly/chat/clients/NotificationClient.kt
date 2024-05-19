
import com.waly.chat.models.FcmToken
import com.waly.chat.models.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationClient {

    @GET("/notifications")
    fun getNotifications(@Header("Authorization") authorization: String): Call<List<Notification>>

    @POST("/users/fcm-token")
    fun saveFcmToken(@Header("Authorization") authorization: String, @Body fcmToken: FcmToken): Call<Void>

}