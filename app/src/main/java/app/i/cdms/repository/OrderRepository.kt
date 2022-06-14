package app.i.cdms.repository

import app.i.cdms.data.source.remote.OrderDataSource
import javax.inject.Inject

class OrderRepository @Inject constructor(private val dataSource: OrderDataSource) :
    BaseRepository() {

    suspend fun fetchOrderList(
        deliveryType: String,
        customerType: String, // kd
        pageNum: Int? = null,
        pageSize: Int? = null,
        beginTime: String,
        endTime: String, // 2022-05-12 00:00:00 2022-05-19%2000%3A00%3A00
        weightState: Int? = null, // 1
        goods: String? = null, // 日用百货
        orderStatus: Int? = null, // 补差状态 1
        senderSearch: Int? = null, // 寄件人姓名/电话/手机
        receiveSearch: Int? = null, // 收件人姓名/电话/手机
        userId: Int? = null, // 下单用户id 2363
        thirdNo: String? = null, // 第三方订单号
        orderNo: String? = null, // 订单号
        deliveryIds: String? = null, // 运单编号中/英文逗号隔开
    ) = executeResponse {
        dataSource.fetchOrderList(
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
    }

    suspend fun updateOrderWeightState(deliveryType: String, orderIds: List<Int>) =
        executeResponse { dataSource.updateOrderWeightState(deliveryType, orderIds) }

    suspend fun batchCancelOrder(deliveryType: String, deliveryIds: List<String>) =
        executeResponse { dataSource.batchCancelOrder(deliveryType, deliveryIds) }
}