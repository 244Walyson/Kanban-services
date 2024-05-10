
import com.waly.chat.models.FullTeam
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TeamClient {

    @GET("/teams/{teamId}")
    fun getTeam(@Path("teamId") teamId: String, @Header("Authorization") authorization: String): Call<FullTeam>

}