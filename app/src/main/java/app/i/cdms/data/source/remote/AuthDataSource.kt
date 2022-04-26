package app.i.cdms.data.source.remote

import app.i.cdms.Constant
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
class AuthDataSource @Inject constructor(private val service: ApiService) {

    suspend fun fetchLoginCaptcha(): Response<YiDaBaseResponse<CaptchaData>> {
        return service.fetchLoginCaptcha()
    }

    suspend fun fetchRegisterCaptcha(phone: String) =
        service.fetchRegisterCaptcha(url = Constant.API_REGISTER_CAPTCHA + "/" + phone)

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

    suspend fun register(
        username: String,
        password: String,
        phone: String,
        phoneCaptcha: String,
        inviteCode: String
    ): Response<YiDaBaseResponse<Any>> {
        val payload = JSONObject()
            .put("userName", username)
            .put("password", password)
            .put("confirmPassword", password)
            .put("phone", phone)
            .put("code", phoneCaptcha)
            .put("inviteCode", inviteCode)
            .toString()
            .toRequestBody("application/json".toMediaType())
        return service.register(payload = payload)
    }
}