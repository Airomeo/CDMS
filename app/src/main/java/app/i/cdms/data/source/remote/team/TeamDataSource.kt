package app.i.cdms.data.source.remote.team

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import java.io.IOException

/**
 * Class that get my team members.
 */
class TeamDataSource(private val service: ApiService) {
    suspend fun getMyTeam(token: Token, pageNum: Int, pageSize: Int): Result<ApiResult<MyTeam>> {
        return try {
            val response =
                service.myTeam(authorization = token.token, pageNum = pageNum, pageSize = pageSize)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception("Error getMyTeam1"))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error getMyTeam" + e.localizedMessage, e))
        }
    }
}