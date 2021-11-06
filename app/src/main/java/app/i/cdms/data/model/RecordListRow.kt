package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecordListRow(
    @Json(name = "accountBalance")
    val accountBalance: String,
    @Json(name = "changeAmount")
    val changeAmount: String,
    @Json(name = "createTime")
    val createTime: String,
    @Json(name = "deliveryId")
    val deliveryId: Any?,
    @Json(name = "earnFrom")
    val earnFrom: Any?,
    @Json(name = "earnFromUserName")
    val earnFromUserName: Any?,
    @Json(name = "id")
    val id: Int,
    @Json(name = "outTradeNo")
    val outTradeNo: Any?,
    @Json(name = "recordType")
    val recordType: String,
    @Json(name = "remark")
    val remark: String,
    @Json(name = "selfCost")
    val selfCost: Any?,
    @Json(name = "tradeNo")
    val tradeNo: Any?,
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "userName")
    val userName: String
)