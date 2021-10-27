package app.i.cdms.data.source.remote.register

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import java.io.IOException

/**
 * Class that add child account.
 */
class RegisterDataSource(private val service: ApiService) {
    suspend fun register(
        token: Token,
        username: String,
        password: String,
        phone: String
    ): Result<ApiResult<Any>> {
        val payload: Map<String, String> = mapOf(
            "nickName" to username,
            "password" to password,
            "phonenumber" to phone,
            "status" to "0",
            "userName" to username
        )
        return try {
            val response = service.addChild(authorization = token.token, payload = payload)

            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception("Error register1"))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error register", e))
        }
    }

}