package app.i.cdms.repository.team

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.data.source.remote.team.TeamDataSource

/**
 * @author ZZY
 * 2021/10/26.
 */
class TeamRepository(private val dataSource: TeamDataSource) {
    suspend fun getMyTeam(pageNum: Int, pageSize: Int): Result<ApiResult<MyTeam>> {
        return dataSource.getMyTeam(pageNum, pageSize)
    }
}