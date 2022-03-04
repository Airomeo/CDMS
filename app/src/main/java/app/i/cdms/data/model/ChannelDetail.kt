package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChannelDetail(
    @Json(name = "calcFeeType")
    val calcFeeType: String?,
    @Json(name = "channelId")
    val channelId: Int,
    @Json(name = "channelName")
    val channelName: String,
    @Json(name = "costPrice")
    val price: Price?,
    @Json(name = "discountPercent")
    val discountPercent: String?,
    @Json(name = "perAdd")
    val perAdd: String?,
    @Json(name = "sort")
    val sort: Int,
    @Json(name = "srArea")
    val srArea: List<String>?,
    val customerChannel: CustomerChannel?,
    var unfold: Boolean = false
)