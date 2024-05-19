
import com.waly.chat.models.CreateUser
import com.waly.chat.models.UriDTO
import com.waly.chat.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserClient {

    @GET("/users/me")
    fun getUser(@Header("Authorization") authorization: String): Call<User>

    @GET("/users")
    fun getUsers(@Header("Authorization") authorization: String): Call<List<User>>

    @POST("/users")
    fun createUser(@Body user: CreateUser): Call<User>
    @PUT("/users/update-image")
    fun updateUserImage(@Header("Authorization") authorization: String, @Body uri: UriDTO): Call<User>

    @POST("/users/connect/{id}")
    fun requestConnection(@Header("Authorization") authorization: String, @Path(value = "id") id: String): Call<User>

    @POST("/users/approve/{id}")
    fun acceptConnection(@Header("Authorization") authorization: String, @Path(value = "id") id: String): Call<Void>

}