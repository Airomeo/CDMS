package app.i.cdms.data.source.remote.register

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import javax.inject.Inject

/**
 * Class that add child account.
 */
class RegisterDataSource @Inject constructor(private val service: ApiService) {
    suspend fun register(
        username: String,
        password: String,
        phone: String
    ): Result<ApiResult<Any>> {
        val payload = JSONObject()
            .put("nickName", username)
            .put("password", password)
            .put("phonenumber", phone)
            .put("status", "0")
            .put("userName", username)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return try {
            val response = service.addChild(payload = payload)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error register", e))
        }
    }
}