
import com.waly.chat.models.CreateUser
import com.waly.chat.models.UriDTO
import com.waly.chat.models.User
import com.waly.chat.models.UserFull
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserClient {

    @GET("/users/users/me")
    fun getUser(@Header("Authorization") authorization: String): Call<User>

    @GET("/users/users/me/full")
    fun findMeFull(@Header("Authorization") authorization: String): Call<UserFull>

    @GET("/users/users")
    fun getUsers(@Header("Authorization") authorization: String): Call<List<User>>

    @POST("/users/users")
    fun createUser(@Body user: CreateUser): Call<User>
    @PUT("/users/users/update-image")
    fun updateUserImage(@Header("Authorization") authorization: String, @Body uri: UriDTO): Call<User>

    @POST("/users/users/connect/{id}")
    fun requestConnection(@Header("Authorization") authorization: String, @Path(value = "id") id: String): Call<User>

    @POST("/users/users/approve/{id}")
    fun acceptConnection(@Header("Authorization") authorization: String, @Path(value = "id") id: String): Call<Void>

}