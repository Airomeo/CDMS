package app.i.cdms.repository.agent

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.OrderCount
import app.i.cdms.data.model.SCFResult
import app.i.cdms.data.model.UserChannelConfig
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

    suspend fun withdraw(userId: Int): ApiResult<Any>? {
        return executeResponse { dataSource.withdraw(userId) }
    }

    suspend fun transfer(
        toUserId: Int,
        userName: String,
        remark: String?,
        recordType: String,
        changeAmount: Float
    ): ApiResult<Any>? {
        return executeResponse {
            dataSource.transfer(
                toUserId,
                userName,
                remark,
                recordType,
                changeAmount
            )
        }
    }

    suspend fun updateChannelByUsername(
        username: String,
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        limitAddCommission: Float
    ): SCFResult? {
        return executeResponse {
            dataSource.updateChannelByUsername(
                username,
                firstWeight,
                firstCommission,
                addCommission,
                limitAddCommission
            )
        }
    }

    suspend fun updateChannelByUserId(
        userId: Int,
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        doConfig: Int
    ): SCFResult? {
        return executeResponse {
            dataSource.updateChannelByUserId(
                userId,
                firstWeight,
                firstCommission,
                addCommission,
                doConfig
            )
        }
    }

    suspend fun getAllChannelConfig(channelId: Int): ApiResult<UserChannelConfig>? {
        return executeResponse { dataSource.getAllChannelConfig(channelId) }
    }
}