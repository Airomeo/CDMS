package app.i.cdms.data.source.remote.team

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.MyTeam
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that get my team members.
 */
class TeamDataSource @Inject constructor(private val service: ApiService) {
    suspend fun getMyTeam(
        pageNum: Int,
        pageSize: Int,
        userName: String?,
        parentUserId: Int?,
        than: String?,
        balance: String?,
    ): Response<MyTeam> {
        return service.myTeam(
            pageNum = pageNum,
            pageSize = pageSize,
            userName = userName,
            parentUserId = parentUserId,
            than = than,
            balance = balance
        )
    }

    suspend fun fetchAgentLevel() = service.fetchAgentLevel()

    suspend fun fetchInviteCode(level: String) =
        service.fetchInviteCode(Constant.API_FETCH_INVITE_CODE + "/" + level)
}