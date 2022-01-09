package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChannelDetail(
    @Json(name = "calcFeeType")
    val calcFeeType: Any?,
    @Json(name = "channelId")
    val channelId: Int,
    @Json(name = "channelName")
    val channelName: String,
    @Json(name = "costPrice")
    val costPrice: CostPrice,
    @Json(name = "discountPercent")
    val discountPercent: Any?,
    @Json(name = "perAdd")
    val perAdd: Any?,
    @Json(name = "sort")
    val sort: Int,
    @Json(name = "srArea")
    val srArea: List<String>,
    @Json(name = "customerChannel")
    var customerChannel: CustomerChannel?,
)