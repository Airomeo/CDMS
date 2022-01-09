package app.i.cdms.repository.agent

import app.i.cdms.data.model.*
import app.i.cdms.data.source.remote.agent.AgentDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/10/26.
 */
class AgentRepository @Inject constructor(private val dataSource: AgentDataSource) :
    BaseRepository() {

    suspend fun getOrderCount(userId: Int): ApiResult<OrderCount>? {
        return executeResponse { dataSource.getOrderCount(userId) }
    }

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
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        limitAddCommission: Float
    ): Result<SCFResult> {
        return dataSource.updateChannelByUsername(
            username,
            firstWeight,
            firstCommission,
            addCommission,
            limitAddCommission
        )
    }

    suspend fun updateChannelByUserId(
        userId: Int,
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        doConfig: Int
    ): Result<SCFResult> {
        return dataSource.updateChannelByUserId(
            userId,
            firstWeight,
            firstCommission,
            addCommission,
            doConfig
        )
    }

    suspend fun getAllChannelConfig(channelId: Int): ApiResult<UserChannelConfig>? {
        return executeResponse { dataSource.getAllChannelConfig(channelId) }
    }
}