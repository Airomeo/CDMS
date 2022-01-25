package app.i.cdms.repository.agent

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.ChannelConfig
import app.i.cdms.data.model.OrderCount
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

    suspend fun getChannelConfigForAllUsers(channelId: Int): ApiResult<List<ChannelConfig>>? {
        return executeResponse { dataSource.getChannelConfigForAllUsers(channelId) }
    }

    suspend fun bindChannelToUser(
        firstCommission: Float?,
        addCommission: Float?,
        channelId: Int,
        customerId: Int,
        discountPercent: Float?,
        perAdd: Float?,
        userIds: List<Int>
    ): ApiResult<Any>? {
        return executeResponse {
            dataSource.bindChannelToUser(
                firstCommission,
                addCommission,
                channelId,
                customerId,
                discountPercent,
                perAdd,
                userIds
            )
        }
    }

    suspend fun updateChildPrice(
        firstCommission: Float?,
        addCommission: Float?,
        channelId: Int,
        customerId: Int,
        discountPercent: Float?,
        perAdd: Float?,
        userIds: List<Int>
    ): ApiResult<Any>? {
        return executeResponse {
            dataSource.updateChildPrice(
                firstCommission,
                addCommission,
                channelId,
                customerId,
                discountPercent,
                perAdd,
                userIds
            )
        }
    }
}