package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CustomerChannel(
    @Json(name = "areaType")
    val areaType: String,
    @Json(name = "backFeeType")
    val backFeeType: String,
    @Json(name = "configJson")
    val configJson: String,
    @Json(name = "createBy")
    val createBy: String,
    @Json(name = "createTime")
    val createTime: String,
    @Json(name = "customerName")
    val customerName: String,
    @Json(name = "customerType")
    val customerType: String,
    @Json(name = "deliveryBusiness")
    val deliveryBusiness: String,
    @Json(name = "deliveryType")
    val deliveryType: String,
    @Json(name = "flag")
    val flag: Int,
    @Json(name = "id")
    val id: Int,
    @Json(name = "isOpen")
    val isOpen: String,
    @Json(name = "lightGoods")
    val lightGoods: String,
    @Json(name = "limitWeight")
    val limitWeight: String,
    @Json(name = "params")
    val params: Params,
    @Json(name = "priority")
    val priority: Int,
    @Json(name = "searchValue")
    val searchValue: Any?,
    @Json(name = "stepCalc")
    val stepCalc: String,
    @Json(name = "upPlatform")
    val upPlatform: String,
    @Json(name = "updateBy")
    val updateBy: String,
    @Json(name = "updateTime")
    val updateTime: String
)