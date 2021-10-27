package app.i.cdms.data.source.remote.main

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class MainDataSource(private val service: ApiService) {

    suspend fun getMyInfo(
        token: Token
    ): Result<ApiResult<MyInfo>> {
        return try {
            val response = service.myInfo(authorization = token.token)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception("Error getM33333333yInfo"))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error getMy111111111Info" + e.localizedMessage, e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}