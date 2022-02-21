package app.i.cdms.data.source.remote.team

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.YiDaBaseResponse
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
    ): Response<YiDaBaseResponse<MyTeam>> {
        return service.myTeam(pageNum = pageNum, pageSize = pageSize, userName = userName)
    }
}