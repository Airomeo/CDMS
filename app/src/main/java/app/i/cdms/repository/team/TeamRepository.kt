package app.i.cdms.repository.team

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.SCFResult
import app.i.cdms.data.source.remote.team.TeamDataSource
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/10/26.
 */
class TeamRepository @Inject constructor(private val dataSource: TeamDataSource) {
    suspend fun getMyTeam(pageNum: Int, pageSize: Int): Result<ApiResult<MyTeam>> {
        return dataSource.getMyTeam(pageNum, pageSize)
    }

    suspend fun batchUpdateChannel(): Result<SCFResult> {
        return dataSource.batchUpdateChannel()
    }
}