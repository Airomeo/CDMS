package app.i.cdms.data.remote.main

import android.util.Log
import app.i.cdms.api.ApiService
import app.i.cdms.data.Result
import app.i.cdms.data.model.*
import java.io.IOException
import java.lang.Exception

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