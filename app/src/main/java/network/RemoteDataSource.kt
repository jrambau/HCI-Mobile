package network

import android.util.Log
import kotlinx.serialization.json.Json
import network.model.NetworkError
import okio.IOException
import retrofit2.Response

abstract class RemoteDataSource{
    private val json = Json { ignoreUnknownKeys = true }
    suspend fun <T : Any> handleApiResponse(execute: suspend () -> Response<T>): T {
      try {
          val response = execute()
          val body = response.body()
          if (response.isSuccessful && body != null) {
              return body
          }
          response.errorBody()?.let {
              val error = json.decodeFromString<NetworkError>(it.string())
              throwDataSourceException(response.code(), error.message)
          }
          throw DataSourceException(response.code(), "Missing error")
      }catch (e: DataSourceException){
          throw e
      }catch (e: IOException){
            throw DataSourceException(0, "Network error")
      }catch (e: Exception){
          Log.e("RemoteDataSource", "unexpected error", e)
          throw DataSourceException(0, "unexpected error")
      }
    }
}
private fun throwDataSourceException(code: Int, message: String): DataSourceException {
    when(code){
        400 -> throw DataSourceException(code, message)
        401 -> throw DataSourceException(code, message)
        403 -> throw DataSourceException(code, message)
        404 -> throw DataSourceException(code, message)
        500 -> throw DataSourceException(code, message)
        else -> throw DataSourceException(code, message)
    }
}