package app.i.cdms.repository.agent

import app.i.cdms.data.model.OrderCount
import app.i.cdms.data.model.YiDaBaseResponse
import app.i.cdms.data.source.remote.agent.AgentDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

class AgentRepository @Inject constructor(private val dataSource: AgentDataSource) :
    BaseRepository() {

    suspend fun getOrderCount(userId: Int): YiDaBaseResponse<OrderCount>? {
        return executeResponse { dataSource.getOrderCount(userId) }
    }

    suspend fun withdraw(userId: Int): YiDaBaseResponse<Any>? {
        return executeResponse { dataSource.withdraw(userId) }
    }

    suspend fun transfer(
        toUserId: Int,
        userName: String,
        remark: String?,
        recordType: String,
        changeAmount: Float
    ): YiDaBaseResponse<Any>? {
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
}