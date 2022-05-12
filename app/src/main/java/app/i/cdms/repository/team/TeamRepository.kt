package app.i.cdms.repository.team

import app.i.cdms.data.model.Agent
import app.i.cdms.data.model.PageOf
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
    ): PageOf<Agent>? {
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
}