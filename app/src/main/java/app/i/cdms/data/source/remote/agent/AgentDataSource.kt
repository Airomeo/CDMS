package app.i.cdms.data.source.remote.agent

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.OrderCount
import app.i.cdms.data.model.YiDaBaseResponse
import retrofit2.Response
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/11/6.
 */
class AgentDataSource @Inject constructor(private val service: ApiService) {

    suspend fun withdraw(userId: Int): Response<YiDaBaseResponse<Any>> {
        return service.clearAccount(userId = userId)
    }

    suspend fun transfer(
        toUserId: Int,
        userName: String,
        remark: String?,
        recordType: String,
        changeAmount: Float
    ): Response<YiDaBaseResponse<Any>> {
        return service.transfer(
            toUserId = toUserId,
            userName = userName,
            remark = remark,
            recordType = recordType,
            changeAmount = changeAmount
        )
    }

    suspend fun getOrderCount(userId: Int): Response<YiDaBaseResponse<OrderCount>> {
        return service.getOrderCount(userId = userId)
    }
}
