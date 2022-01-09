package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserChannelConfig(
    @Json(name = "userName")
    val userName: String,
    @Json(name = "user_id")
    val userId: Int,
    @Json(name = "channelId")
    val channelId: Int?,
    @Json(name = "channelName")
    val channelName: String?,
    @Json(name = "calcFeeType")
    val calcFeeType: String?,
    @Json(name = "discountPercent")
    val discountPercent: Any?,
    @Json(name = "perAdd")
    val perAdd: Any?,
    @Json(name = "firstProfit")
    val firstProfit: String?,
    @Json(name = "addProfit")
    val addProfit: String?
)