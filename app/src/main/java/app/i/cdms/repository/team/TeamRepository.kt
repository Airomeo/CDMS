package app.i.cdms.repository.team

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.data.source.remote.team.TeamDataSource
import app.i.cdms.repository.UserPrefRepository
import kotlinx.coroutines.flow.first

/**
 * @author ZZY
 * 2021/10/26.
 */
class TeamRepository(
    private val dataSource: TeamDataSource,
    private val userPrefRepository: UserPrefRepository
) {
    suspend fun getMyTeam(pageNum: Int, pageSize: Int): Result<ApiResult<MyTeam>> {
        return dataSource.getMyTeam(userPrefRepository.tokenFlow.first(), pageNum, pageSize)
    }
}