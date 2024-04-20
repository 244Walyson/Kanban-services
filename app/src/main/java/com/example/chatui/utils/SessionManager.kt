
import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_LOGGED_NICKNAME = "user_logged_nickname"
    }


    var accessToken: String?
        get() = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            editor.putString(KEY_ACCESS_TOKEN, value)
            editor.apply()
        }

    var userLogged: String?
        get() = sharedPreferences.getString(KEY_USER_LOGGED_NICKNAME, null)
        set(value) {
            editor.putString(KEY_USER_LOGGED_NICKNAME, value)
            editor.apply()
        }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}