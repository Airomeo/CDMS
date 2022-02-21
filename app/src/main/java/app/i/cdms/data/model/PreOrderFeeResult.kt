package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PreOrderFeeResult(
    @Json(name = "backFeeType")
    val backFeeType: String,
    @Json(name = "calcFeeType")
    val calcFeeType: String,
    @Json(name = "channelName")
    val channelName: String,
    @Json(name = "deliveryType")
    val deliveryType: String,
    @Json(name = "discountPercent")
    val discountPercent: Any?,
    @Json(name = "lightGoods")
    val lightGoods: String,
    @Json(name = "limitWeight")
    val limitWeight: String,
    @Json(name = "perAdd")
    val perAdd: Any?,
    @Json(name = "preBjFee")
    val preBjFee: Any?,
    @Json(name = "preOrderFee")
    val preOrderFee: String,
    @Json(name = "price")
    val price: Price,
    @Json(name = "stepCalc")
    val stepCalc: String,
    @Json(name = "userId")
    val userId: Any?
)