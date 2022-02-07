package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CustomerChannel(
    @Json(name = "areaType")
    val areaType: String,
    @Json(name = "backFeeType")
    val backFeeType: String,
    @Json(name = "customerName")
    val customerName: String,
    @Json(name = "customerType")
    val customerType: String,
    @Json(name = "deliveryBusiness")
    val deliveryBusiness: String,
    @Json(name = "deliveryType")
    val deliveryType: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "lightGoods")
    val lightGoods: String,
    @Json(name = "limitWeight")
    val limitWeight: String,
    @Json(name = "priority")
    val priority: Int,
    @Json(name = "stepCalc")
    val stepCalc: String
)