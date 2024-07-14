
import com.waly.chat.models.FullTeam
import com.waly.chat.models.TeamFullResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface TeamClient {

    @GET("/kanban/teams/{teamId}")
    fun getTeam(@Path("teamId") teamId: String, @Header("Authorization") authorization: String): Call<FullTeam>

    @GET("/teams")
    fun getTeams(@Header("Authorization") authorization: String): Call<TeamFullResponse>

}