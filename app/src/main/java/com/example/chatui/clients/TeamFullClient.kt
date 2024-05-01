
import com.example.chatui.models.FullTeam
import com.example.chatui.models.TeamFullResponse
import com.example.chatui.models.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TeamFullClient {

    @GET("/teams")
    fun getTeams(@Header("Authorization") authorization: String): Call<TeamFullResponse>

}