package app.i.cdms.repository.agent

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.source.remote.agent.AgentDataSource

/**
 * @author ZZY
 * 2021/10/26.
 */
class AgentRepository(private val dataSource: AgentDataSource) {

    suspend fun withdraw(userId: Int): Result<ApiResult<Any>> {
        return dataSource.withdraw(userId)
    }

    suspend fun transfer(
        toUserId: Int,
        userName: String,
        remark: String?,
        recordType: String,
        changeAmount: Float
    ): Result<ApiResult<Any>> {
        return dataSource.transfer(toUserId, userName, remark, recordType, changeAmount)
    }
}