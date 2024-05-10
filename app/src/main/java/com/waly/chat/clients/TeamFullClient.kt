
import com.waly.chat.models.TeamFullResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface TeamFullClient {

    @GET("/teams")
    fun getTeams(@Header("Authorization") authorization: String): Call<TeamFullResponse>

}