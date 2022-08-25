package app.i.cdms.data.source.remote

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.CancelOrderResult
import app.i.cdms.data.model.YiDaBaseResponse
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class OrderDataSource @Inject constructor(private val service: ApiService) {

    suspend fun fetchOrderList(
        deliveryType: String,
        customerType: String, // kd
        pageNum: Int?,
        pageSize: Int?,
        beginTime: String,
        endTime: String, // 2022-05-12 00:00:00 2022-05-19%2000%3A00%3A00
        weightState: Int?, // 1
        goods: String?, // 日用百货
        orderStatus: Int?, // 补差状态 1
        senderSearch: Int?, // 寄件人姓名/电话/手机
        receiveSearch: Int?, // 收件人姓名/电话/手机
        userId: Int?, // 下单用户id 2363
        thirdNo: String?, // 第三方订单号
        orderNo: String?, // 订单号
        deliveryIds: String?, // 运单编号中/英文逗号隔开
    ) = service.fetchOrderList(
        Constant.API_FETCH_ORDER,
        deliveryType,
        customerType,
        pageNum,
        pageSize,
        beginTime,
        endTime,
        weightState,
        goods,
        orderStatus,
        senderSearch,
        receiveSearch,
        userId,
        thirdNo,
        orderNo,
        deliveryIds
    )

    suspend fun updateOrderWeightState(
        deliveryType: String,
        orderIds: List<Int>
    ): Response<YiDaBaseResponse<Boolean>> {
        val payload = JSONObject()
            .put("deliveryType", deliveryType)
            .put("ids", JSONArray(orderIds))
            .toString()
            .toRequestBody("application/json".toMediaType())
        return service.updateOrderWeightState(payload = payload)
    }

    suspend fun batchCancelOrder(
        deliveryType: String,
        deliveryIds: List<String>
    ): Response<YiDaBaseResponse<List<CancelOrderResult>>> {
        var str = ""
        for (item in deliveryIds) {
            str = "$str,$item"
        }
        str = str.substring(1)
        return service.batchCancelOrder(url = "${Constant.API_UPDATE_ORDER_BATCH_CANCEL}/$deliveryType/$str")
    }
}