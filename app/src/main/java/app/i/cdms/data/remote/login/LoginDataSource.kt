package app.i.cdms.data.remote.login

import app.i.cdms.api.ApiService
import app.i.cdms.data.Result
import app.i.cdms.data.model.*
import java.io.IOException
import java.lang.Exception

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val service: ApiService) {

    suspend fun getCaptcha(): Result<ApiResult<CaptchaData>> {
        return try {
            val response = service.getCaptcha()

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception("Error get CAPTCHA"))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error get CAPTCHA", e))
        }
    }

    suspend fun login(
        username: String,
        password: String,
        captcha: Int,
        uuid: String
    ): Result<ApiResult<Token>> {
        val payload: Map<String, String> = mapOf(
            "username" to username,
            "password" to password,
            "code" to captcha.toString(),
            "uuid" to uuid
        )
        return try {
            val response = service.login(payload = payload)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception("Error login1"))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error login", e))
        }
    }
}