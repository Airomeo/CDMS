package app.i.cdms.repository.agent

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.SCFResult
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

    suspend fun updateChannelByUsername(
        username: String,
        firstCommission: Float,
        additionalCommission: Float
    ): Result<SCFResult> {
        return dataSource.updateChannelByUsername(username, firstCommission, additionalCommission)
    }

    suspend fun updateChannelByUserId(
        userId: Int,
        firstCommission: Float,
        additionalCommission: Float
    ): Result<SCFResult> {
        return dataSource.updateChannelByUserId(userId, firstCommission, additionalCommission)
    }
}