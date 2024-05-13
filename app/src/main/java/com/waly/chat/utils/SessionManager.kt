
import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_LOGGED_NICKNAME = "user_logged_nickname"
        private const val KEY_USER_LOGGED_NAME = "user_logged_name"
        private const val KEY_USER_LOGGED_IMG = "user_logged_imgUrl"
        private const val KEY_ACCESS_TOKEN_EXPIRATION = "access_token_expiration"
        private const val KEY_TOKEN_SAVED = "token_saved"
    }

    var userLoggedName: String?
        get() = sharedPreferences.getString(KEY_USER_LOGGED_NAME, null)
        set(value) {
            editor.putString(KEY_USER_LOGGED_NAME, value)
            editor.apply()
        }

    var userLoggedImg: String?
        get() = sharedPreferences.getString(KEY_USER_LOGGED_IMG, null)
        set(value) {
            editor.putString(KEY_USER_LOGGED_IMG, value)
            editor.apply()
        }

    var accessToken: String?
        get() = sharedPreferences.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            editor.putString(KEY_ACCESS_TOKEN, value)
            editor.apply()
        }

    var accessTokenExpiration: String?
        get() = sharedPreferences.getString(KEY_ACCESS_TOKEN_EXPIRATION, null)
        set(value) {
            editor.putString(KEY_ACCESS_TOKEN_EXPIRATION, value)
            editor.apply()
        }

    var userLogged: String?
        get() = sharedPreferences.getString(KEY_USER_LOGGED_NICKNAME, null)
        set(value) {
            editor.putString(KEY_USER_LOGGED_NICKNAME, value)
            editor.apply()
        }

    var tokenSaved: Boolean
        get() = sharedPreferences.getBoolean(KEY_TOKEN_SAVED, false)
        set(value) {
            editor.putBoolean(KEY_TOKEN_SAVED, value)
            editor.apply()
        }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}