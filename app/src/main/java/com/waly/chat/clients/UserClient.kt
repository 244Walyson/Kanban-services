
import com.waly.chat.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UserClient {

    @GET("/users/me")
    fun getUser(@Header("Authorization") authorization: String): Call<User>

}