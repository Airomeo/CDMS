package app.i.cdms.data.source.remote.home

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Result
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class HomeDataSource(private val service: ApiService) {

    suspend fun getMyInfo(): Result<ApiResult<MyInfo>> {
        return try {
            val response = service.myInfo()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error HomeDataSource.getMyInfo" + e.localizedMessage, e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}