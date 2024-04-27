
import com.example.chatui.models.FullTeam
import com.example.chatui.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TeamClient {

    @GET("/chat-room/{teamId}")
    fun getTeam(@Path("teamId") teamId: String, @Header("Authorization") authorization: String): Call<FullTeam>

}