package app.i.cdms.data.source.remote.agent

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ChannelConfig
import app.i.cdms.data.model.OrderCount
import app.i.cdms.data.model.YiDaBaseResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
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

    suspend fun getChannelConfigForAllUsers(channelId: Int): Response<YiDaBaseResponse<List<ChannelConfig>>> {
        return service.getChannelConfigForAllUsers(Constant.API_GET_CHILDREN_PRICE_BY_CHANNEL + "/$channelId")
    }

    suspend fun getOrderCount(userId: Int): Response<YiDaBaseResponse<OrderCount>> {
        return service.getOrderCount(userId = userId)
    }

    // 根据用户ID配置渠道
    suspend fun bindChannelToUser(
        firstCommission: Float?,
        addCommission: Float?,
        channelId: Int,
        customerId: Int,
        discountPercent: Float?,
        perAdd: Float?,
        userIds: List<Int>
    ): Response<YiDaBaseResponse<Any>> {
        val payload = JSONObject()
            .put("firstProfit", firstCommission)
            .put("addProfit", addCommission)
            .put("channelId", channelId)
            .put("customerId", customerId)
            .put("discountPercent", discountPercent)
            .put("perAdd", perAdd)
            .put("userIds", JSONArray(userIds))
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.bindChannelToUser(payload = payload)
    }

    // 根据用户ID更新渠道配置
    suspend fun updateChildPrice(
        firstCommission: Float?,
        addCommission: Float?,
        channelId: Int,
        customerId: Int,
        discountPercent: Float?,
        perAdd: Float?,
        userIds: List<Int>
    ): Response<YiDaBaseResponse<Any>> {
        val payload = JSONObject()
            .put("firstProfit", firstCommission)
            .put("addProfit", addCommission)
            .put("channelId", channelId)
            .put("customerId", customerId)
            .put("discountPercent", discountPercent)
            .put("perAdd", perAdd)
            .put("userIds", JSONArray(userIds))
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.updateChildPrice(payload = payload)
    }

    // 根据用户ID更新渠道配置
    suspend fun deleteChildPrice(
        channelId: Int,
        userIds: List<Int>
    ): Response<YiDaBaseResponse<Any>> {
        val userIdsStr = userIds.toString().drop(1).dropLast(1)
        val url = Constant.API_DELETE_CHILD_PRICE + "/" + channelId + "/" + userIdsStr
        return service.deleteChildPrice(url)
    }
}
