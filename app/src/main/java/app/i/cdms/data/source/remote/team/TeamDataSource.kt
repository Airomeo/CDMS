package app.i.cdms.data.source.remote.team

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.SCFResult
import java.io.IOException
import javax.inject.Inject

/**
 * Class that get my team members.
 */
class TeamDataSource @Inject constructor(private val service: ApiService) {
    suspend fun getMyTeam(pageNum: Int, pageSize: Int): Result<ApiResult<MyTeam>> {
        return try {
            val response = service.myTeam(pageNum = pageNum, pageSize = pageSize)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error getMyTeam" + e.localizedMessage, e))
        }
    }

    suspend fun batchUpdateChannel(): Result<SCFResult> {
        return try {
            val response = service.batchUpdateChannel()
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error batchUpdateChannel" + e.localizedMessage, e))
        }
    }
}