import android.content.Context
import android.content.SharedPreferences


class SessionManager(context: Context) {
    private var preferences: SharedPreferences =
        context.getSharedPreferences("session", Context.MODE_PRIVATE)


    fun saveAuthToken(token: String) {
        val editor = preferences.edit()
        editor.putString("authToken", token)
        editor.apply()
    }

    fun loadAuthToken(): String? {
        return preferences.getString("authToken", null)
    }
    fun removeAuthToken() {
        val editor = preferences.edit()
        editor.remove("authToken")
        editor.apply()
    }
}