package app.i.cdms.data.source.remote.channel

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.YiDaBaseResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class ChannelDataSource @Inject constructor(private val service: ApiService) {
    suspend fun getCustomerChannelPrice(customerId: Int, area: Boolean = false) =
        service.getCustomerChannelPrice(if (area) Constant.API_CUSTOMER_CHANNEL_PRICE else Constant.API_CUSTOMER_CHANNEL_PRICE_NO_AREA + "/" + customerId.toString())

    suspend fun fetchBindUserPrice(channelId: Int, pageNum: Int, pageSize: Int) =
        service.fetchBindUserPrice(channelId = channelId, pageNum = pageNum, pageSize = pageSize)

    suspend fun fetchUserPrice(channelId: Int, isBind: Boolean) =
        service.fetchUserPrice(channelId = channelId, isBind = isBind)

    // 根据用户ID配置渠道
    suspend fun addUserPrice(
        calcFeeType: String,
        channelId: Int,
        price: String,
        userIds: List<Int>
    ): Response<YiDaBaseResponse<Any>> {
        val payload = JSONObject()
            .put("calcFeeType", calcFeeType) // profit
            .put("channelId", channelId) // 123
            .put("price", price) // 1-3=5.4+1.4;4-50=11+2
            .put("userIds", JSONArray(userIds)) // [8964, 8989]
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.addUserPrice(payload = payload)
    }

    // 根据用户ID更新渠道配置
    suspend fun updateUserPrice(
        calcFeeType: String,
        channelId: Int,
        price: String,
        userIds: List<Int>
    ): Response<YiDaBaseResponse<Any>> {
        val payload = JSONObject()
            .put("calcFeeType", calcFeeType) // profit
            .put("channelId", channelId) // 123
            .put("price", price) // 1-3=5.4+1.4;4-50=11+2
            .put("userIds", JSONArray(userIds)) // [8964,8989]
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.updateUserPrice(payload = payload)
    }

    // 根据用户的渠道ID删除渠道配置
    suspend fun deleteUserPrice(childChannelPriceIds: List<Int>): Response<YiDaBaseResponse<Any>> {
        var str = ""
        childChannelPriceIds.forEach { str = "$str$it," }
        str = str.dropLast(1)

        val url = Constant.API_DELETE_USER_PRICE + "/" + str
//        https://www.yida178.cn/prod-api/price/remove/411616,298002
        return service.deleteChildPrice(url)
    }
}
