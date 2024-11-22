import android.content.Context
import android.content.SharedPreferences


class SessionManager(private val context: Context) {
    private val preferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun loadAuthToken(): String? {
        return preferences.getString("auth_token", null)
    }

    fun saveAuthToken(token: String) {
        preferences.edit().putString("auth_token", token).apply()
    }

    fun removeAuthToken() {
        preferences.edit().remove("auth_token").apply()
    }
}