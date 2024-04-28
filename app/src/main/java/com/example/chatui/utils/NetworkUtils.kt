
import com.example.chatui.clients.SaveFcmTokenClient
import com.example.chatui.utils.Environments
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {

    companion object{
        private lateinit var INSTANCE: Retrofit
        private lateinit var INSTANCE_CHAT: Retrofit
        private val BASE_URL_CHAT = Environments.BASE_CHAT_URL
        private val BASE_URL = Environments.BASE_KANBAN_URL


        private fun getRetrofitInstance(): Retrofit {
            if (!::INSTANCE.isInitialized) {
                val htttp = OkHttpClient.Builder();
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(htttp.build())
                    .build()
            }
            return INSTANCE
        }

        private fun getRetrofitInstanceChat(): Retrofit {
            if (!::INSTANCE_CHAT.isInitialized) {
                val htttp = OkHttpClient.Builder();
                INSTANCE_CHAT = Retrofit.Builder()
                    .baseUrl(BASE_URL_CHAT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(htttp.build())
                    .build()
            }
            return INSTANCE_CHAT
        }


        fun createServiceLogin(): LoginClient {
            return getRetrofitInstance().create(LoginClient::class.java)
        }

        fun createServiceUser(): UserClient {
            return getRetrofitInstance().create(UserClient::class.java)
        }

        fun createServiceSaveToken(): SaveFcmTokenClient {
            return getRetrofitInstanceChat().create(SaveFcmTokenClient::class.java)
        }

        fun createServiceTeam(): TeamClient {
            return getRetrofitInstance().create(TeamClient::class.java)
        }
    }
}