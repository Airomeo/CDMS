package app.i.cdms.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

data class OrderItem(
    val deliveryType: String,
    val deliveryId: String,
    val userName: String,
    val senderName: String,
    val senderAddress: String,
    val receiverName: String,
    val receiverAddress: String,
    val orderState: OrderState,
    val orderStateDetails: String?,
    val weightState: String?,
)

@Parcelize
@JsonClass(generateAdapter = true)
data class RawOrder(
    @Json(name = "addFee")
    val addFee: String?, // null
    @Json(name = "backDeduct")
    val backDeduct: String?, // N
    @Json(name = "backFee")
    val backFee: String?, // null
    @Json(name = "bigWord")
    val bigWord: String?, // null
    @Json(name = "bjFee")
    val bjFee: String?, // null
    @Json(name = "channelId")
    val channelId: Int?, // 148 中通的
    @Json(name = "channelName")
    val channelName: String, // 圆通22P艾悦-适用3kg内渠道1区
    @Json(name = "collectionMoney")
    val collectionMoney: String?, // null 中通的
    @Json(name = "createTime")
    val createTime: String, // 2022-05-17 15:37:13
    @Json(name = "customerId")
    val customerId: Int?, // 110
    @Json(name = "customerType")
    val customerType: String?, // kd 中通的
    @Json(name = "data")
    val `data`: String?, // 555E12416804A674FD7F984DE93DE267 中通的
    @Json(name = "deliveryId")
    val deliveryId: String, // YT2218796787166
    @Json(name = "feeBlock")
    val feeBlock: String?, // [{"fee":0,"type":0,"name":"实收快递费"}]
    @Json(name = "finalCalcWeight")
    val finalCalcWeight: String?, // null
    @Json(name = "flag")
    val flag: Int?, // 0
    @Json(name = "goods")
    val goods: String, // 日用百货
    @Json(name = "guaranteeValueAmount")
    val guaranteeValueAmount: String?, // null
    @Json(name = "hasDeduct")
    val hasDeduct: String?, // N
    @Json(name = "hasEarn")
    val hasEarn: String?, // N 中通的
    @Json(name = "hasReturn")
    val hasReturn: String?, // N
    @Json(name = "id")
    val id: Int, // 663431
    @Json(name = "lgWeight")
    val lgWeight: String?, // null
    @Json(name = "orderFee")
    val orderFee: String?, // 0.0
    @Json(name = "orderNo")
    val orderNo: String, // YT20220517153713308488
    @Json(name = "orderStatus")
    val orderStatus: String, // 1
    @Json(name = "otherFee")
    val otherFee: String?, // null
    @Json(name = "otherFeeDetail")
    val otherFeeDetail: String?, // null
    @Json(name = "overFee")
    val overFee: String?, // null
    @Json(name = "packageCount")
    val packageCount: Int, // 1
    @Json(name = "packageFee")
    val packageFee: String?, // null
    @Json(name = "pdfUrl")
    val pdfUrl: String?, // http://b.stosolution.com/api/label/getLabel?param=Z28rbmI1NVYwZ2hiRHFQd0hPZENaUTNlc0xURmxaeXBQK2pxeTZOazVwTT0
    @Json(name = "pickUpEndTime")
    val pickUpEndTime: String?, // 2022-05-17 17:00:00
    @Json(name = "pickUpStartTime")
    val pickUpStartTime: String?, // 2022-05-17 16:00:00
    @Json(name = "preOrderFee")
    val preOrderFee: String, // 11.0
    @Json(name = "qty")
    val qty: Int?, // 1
    @Json(name = "realOrderState")
    val realOrderState: String?, // 快递员已接单：黄小云18396525457
    @Json(name = "realVolume")
    val realVolume: String?, // null
    @Json(name = "realWeight")
    val realWeight: String?, // null or 2.25
    @Json(name = "receiveAddress")
    val receiveAddress: String, // 福建省福州市平潭县潭城镇崀山大道888号-1
    @Json(name = "receiveMobile")
    val receiveMobile: String?, // 18890199899
    @Json(name = "receiveName")
    val receiveName: String, // (系统ID）转信品国际
    @Json(name = "receiveTel")
    val receiveTel: String?,
    @Json(name = "remark")
    val remark: String?, // null
    @Json(name = "rightOrder")
    val rightOrder: String?, // N
    @Json(name = "senderAddress")
    val senderAddress: String, // 福建省福州市鼓楼区软件园A区29号楼205
    @Json(name = "senderMobile")
    val senderMobile: String?, // 13295970684
    @Json(name = "senderName")
    val senderName: String, // Ever
    @Json(name = "senderTel")
    val senderTel: String?,
    @Json(name = "settlementError")
    val settlementError: String?, // null 德邦的
    @Json(name = "signedTime")
    val signedTime: String?, // null 德邦的
    @Json(name = "thirdNo")
    val thirdNo: String?, // null
    @Json(name = "unitPrice")
    val unitPrice: String?, // 2000.0
    @Json(name = "updateTime")
    val updateTime: String?, // 2022-05-12 15:08:30 德邦的
    @Json(name = "userId")
    val userId: Int, // 2376
    @Json(name = "userName")
    val userName: String, // 林梅云1
    @Json(name = "volume")
    val volume: String, // 0.0
    @Json(name = "weight")
    val weight: String, // 5.0
    @Json(name = "weightState")
    val weightState: String?, // null
    val deliveryType: String?, // 自己加的属性，用于简化业务逻辑。手动赋值保证非空
) : Parcelable {
    @IgnoredOnParcel
    val orderItem by lazy {
        OrderItem(
            deliveryType!!,
            deliveryId,
            userName,
            senderName,
            senderAddress,
            receiveName,
            receiveAddress,
            when (orderStatus) {
                "1" -> OrderState.WAITING
                "2" -> OrderState.DELIVERING
                "3" -> OrderState.RECEIVED
                "6" -> OrderState.ERROR
                "10" -> OrderState.CANCELED
                else -> OrderState.ERROR
            },
            realOrderState,
            weightState
        )
    }
}

@JsonClass(generateAdapter = true)
data class CancelOrderResult(
    @Json(name = "deliveryId")
    val deliveryId: String, // 775630020760988
    @Json(name = "errMsg")
    val errMsg: String, // 成功
    @Json(name = "success")
    val success: Boolean // true
)

enum class OrderState(val key: String) {
    WAITING("1"),
    DELIVERING("2"),
    RECEIVED("3"),
    ERROR("6"),
    CANCELED("10"),
}

enum class WeightState(val key: String?) {
    UNKNOWN(null),
    NORMAL("1"),
    OVERWEIGHT("2"),
    WEIGHTLESSNESS("3"),
    DONE("4"),
}

//enum class DeliveryType() {
//    DOP, JD, STO-INT, YTO,JT, SF, ZTO
//}

