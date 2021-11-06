package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyPriceItem(
    @Json(name = "addPrice")
    val addPrice: String,
    @Json(name = "backType")
    val backType: Int,
    @Json(name = "channelId")
    val channelId: Int,
    @Json(name = "channelName")
    val channelName: String,
    @Json(name = "createBy")
    val createBy: String,
    @Json(name = "createTime")
    val createTime: String,
    @Json(name = "firstPrice")
    val firstPrice: String,
    @Json(name = "firstWeight")
    val firstWeight: String,
    @Json(name = "flag")
    val flag: Int,
    @Json(name = "id")
    val id: Int,
    @Json(name = "isSpecial")
    val isSpecial: Int,
    @Json(name = "limitAddPrice")
    val limitAddPrice: String,
    @Json(name = "limitFirstPrice")
    val limitFirstPrice: String,
    @Json(name = "limitWeight")
    val limitWeight: Double,
    @Json(name = "params")
    val params: Params,
    @Json(name = "priority")
    val priority: String,
    @Json(name = "searchValue")
    val searchValue: Any?,
    @Json(name = "sendReceiveProvinces")
    val sendReceiveProvinces: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "updateBy")
    val updateBy: String,
    @Json(name = "updateTime")
    val updateTime: String?,
    @Json(name = "userId")
    val userId: Int
)