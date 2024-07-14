
import com.waly.chat.models.UriDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface FileClient {

    @Multipart
    @POST("/file/image")
    fun uploadFile(@Header("Authorization") authorization: String, @Part file: MultipartBody.Part): Call<UriDTO>

}