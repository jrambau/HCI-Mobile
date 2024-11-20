
import android.annotation.SuppressLint
import android.content.Context
import okhttp3.Interceptor

import retrofit2.http.POST
class AuthInterceptor(context: Context) : Interceptor {
   private val sessionManager = SessionManager(context)
    @SuppressLint("SuspiciousIndentation")
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val requestBuilder = chain.request().newBuilder()

          sessionManager.loadAuthToken()?.let {
              requestBuilder.addHeader("Authorization", "Bearer $it")
          }
        return chain.proceed(requestBuilder.build())
    }
}