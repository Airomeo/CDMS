package app.i.cdms.repository.team

import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.source.remote.team.TeamDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/10/26.
 */
class TeamRepository @Inject constructor(private val dataSource: TeamDataSource) :
    BaseRepository() {
    suspend fun getMyTeam(
        pageNum: Int,
        pageSize: Int,
        userName: String?,
        parentUserId: Int?,
        than: String?,
        balance: String?,
    ): MyTeam? {
        return executeResponse {
            dataSource.getMyTeam(
                pageNum,
                pageSize,
                userName,
                parentUserId,
                than,
                balance
            )
        }
    }

    suspend fun fetchAgentLevel() = executeResponse { dataSource.fetchAgentLevel() }

    suspend fun fetchInviteCode(level: String) =
        executeResponse { dataSource.fetchInviteCode(level) }
}