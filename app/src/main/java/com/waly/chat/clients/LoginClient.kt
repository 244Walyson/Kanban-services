
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface LoginClient {

    @FormUrlEncoded
    @POST("/users/oauth2/token")
    fun login(
        @Header("Authorization") authorization: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String
    ) : Call<LoginResponse>
}