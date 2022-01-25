package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChannelConfig(
    @Json(name = "userName")
    val userName: String,
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "channelId")
    val channelId: Int?,
    @Json(name = "channelName")
    val channelName: String?,
    @Json(name = "calcFeeType")
    val calcFeeType: String?,
    @Json(name = "discountPercent")
    val discountPercent: String?,
    @Json(name = "perAdd")
    val perAdd: String?,
    @Json(name = "firstProfit")
    val firstProfit: String?,
    @Json(name = "addProfit")
    val addProfit: String?
)