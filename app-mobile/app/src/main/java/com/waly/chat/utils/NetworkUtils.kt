
import com.waly.chat.clients.SaveFcmTokenClient
import com.waly.chat.utils.Environments
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class NetworkUtils {

    companion object{
        private lateinit var INSTANCE: Retrofit
        private val BASE_URL = Environments.BASE_URL


        private fun getRetrofitInstance(): Retrofit {
            if (!::INSTANCE.isInitialized) {
                val client = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build()

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
            return INSTANCE
        }

        fun createServiceLogin(): LoginClient {
            return getRetrofitInstance().create(LoginClient::class.java)
        }

        fun createServiceUser(): UserClient {
            return getRetrofitInstance().create(UserClient::class.java)
        }

        fun createServiceSaveToken(): SaveFcmTokenClient {
            return getRetrofitInstance().create(SaveFcmTokenClient::class.java)
        }

        fun createServiceTeam(): TeamClient {
            return getRetrofitInstance().create(TeamClient::class.java)
        }

        fun createServiceNotification(): NotificationClient {
            return getRetrofitInstance().create(NotificationClient::class.java)
        }

        fun createServiceFile(): FileClient {
            return getRetrofitInstance().create(FileClient::class.java)
        }
    }
}