package app.i.cdms.data.source.remote.agent

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.OrderCount
import app.i.cdms.data.model.SCFResult
import app.i.cdms.data.model.UserChannelConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/11/6.
 */
class AgentDataSource @Inject constructor(private val service: ApiService) {

    suspend fun withdraw(userId: Int): Response<ApiResult<Any>> {
        return service.clearAccount(userId = userId)
    }

    suspend fun transfer(
        toUserId: Int,
        userName: String,
        remark: String?,
        recordType: String,
        changeAmount: Float
    ): Response<ApiResult<Any>> {
        return service.transfer(
            toUserId = toUserId,
            userName = userName,
            remark = remark,
            recordType = recordType,
            changeAmount = changeAmount
        )
    }

    // 根据用户名配置价格
    suspend fun updateChannelByUsername(
        username: String,
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        limitAddCommission: Float
    ): Response<SCFResult> {
        val payload = JSONObject()
            .put("username", username)
            .put("firstWeight", firstWeight)
            .put("firstCommission", firstCommission)
            .put("addCommission", addCommission)
            .put("limitAddCommission", limitAddCommission)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.updateChannelByUsername(payload = payload)
    }

    // 根据用户ID配置价格
    suspend fun updateChannelByUserId(
        userId: Int,
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        doConfig: Int
    ): Response<SCFResult> {
        val payload = JSONObject()
            .put("userId", userId)
            .put("firstWeight", firstWeight)
            .put("firstCommission", firstCommission)
            .put("addCommission", addCommission)
            .put("doConfig", doConfig) // 0:Nothing, 1:addUserConfig, 2:updateUserConfig
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.updateChannelByUserId(payload = payload)
    }

    suspend fun getAllChannelConfig(channelId: Int): Response<ApiResult<UserChannelConfig>> {
        return service.getAllChannelConfig(Constant.API_GET_CHILDREN_PRICE_BY_CHANNEL + "/$channelId")
    }

    suspend fun getOrderCount(userId: Int): Response<ApiResult<OrderCount>> {
        return service.getOrderCount(userId = userId)
    }
}
