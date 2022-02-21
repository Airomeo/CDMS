package app.i.cdms.data.source.remote.register

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.YiDaBaseResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that add child account.
 */
class RegisterDataSource @Inject constructor(private val service: ApiService) {
    suspend fun register(
        username: String,
        password: String,
        phone: String
    ): Response<YiDaBaseResponse<Any>> {
        val payload = JSONObject()
            .put("nickName", username)
            .put("password", password)
            .put("phonenumber", phone)
            .put("status", "0")
            .put("userName", username)
            .toString()
            .toRequestBody("application/json".toMediaType())
        return service.addChild(payload = payload)
    }
}