package app.i.cdms.data.source.remote.register

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.SCFResult
import java.io.IOException

/**
 * Class that add child account.
 */
class RegisterDataSource(private val service: ApiService) {
    suspend fun register(
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

    // 根据用户名配置价格
    suspend fun updateChannelByUsername(
        username: String,
        firstCommission: Float,
        additionalCommission: Float
    ): Result<SCFResult> {
        val payload: Map<String, Any> = mapOf(
            "username" to username,
            "firstCommission" to firstCommission,
            "additionalCommission" to additionalCommission
        )
        return try {
            val response = service.updateChannelByUsername(payload = payload)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("updateChannelByUsername fail", e))
        }
    }
}