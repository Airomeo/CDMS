package app.i.cdms.repository.agent

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.source.remote.agent.AgentDataSource
import app.i.cdms.repository.UserPrefRepository
import kotlinx.coroutines.flow.first

/**
 * @author ZZY
 * 2021/10/26.
 */
class AgentRepository(
    private val dataSource: AgentDataSource, private val userPrefRepository: UserPrefRepository
) {

    suspend fun withdraw(userId: Int): Result<ApiResult<Any>> {
        return dataSource.withdraw(userPrefRepository.tokenFlow.first(), userId)
    }

    suspend fun transfer(
        toUserId: Int,
        userName: String,
        remark: String?,
        recordType: String,
        changeAmount: Float
    ): Result<ApiResult<Any>> {
        return dataSource.transfer(
            userPrefRepository.tokenFlow.first(),
            toUserId,
            userName,
            remark,
            recordType,
            changeAmount
        )
    }
}