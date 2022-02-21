package app.i.cdms.data.source.remote.login

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.data.model.Token
import app.i.cdms.data.model.YiDaBaseResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource @Inject constructor(private val service: ApiService) {

    suspend fun getCaptcha(): Response<YiDaBaseResponse<CaptchaData>> {
        return service.getCaptcha()
    }

    suspend fun login(
        username: String,
        password: String,
        captcha: Int,
        uuid: String
    ): Response<YiDaBaseResponse<Token>> {
        val payload = JSONObject()
            .put("username", username)
            .put("password", password)
            .put("code", captcha.toString())
            .put("uuid", uuid)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.login(payload = payload)
    }
}