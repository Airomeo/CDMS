package app.i.cdms.data.source.remote.team

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.SCFResult
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that get my team members.
 */
class TeamDataSource @Inject constructor(private val service: ApiService) {
    suspend fun getMyTeam(
        pageNum: Int,
        pageSize: Int,
        userName: String?
    ): Response<ApiResult<MyTeam>> {
        return service.myTeam(pageNum = pageNum, pageSize = pageSize, userName = userName)
    }

    suspend fun batchUpdateChannel(): Response<SCFResult> {
        return service.batchUpdateChannel()
    }
}