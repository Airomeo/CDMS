package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Agent(
    @Json(name = "accountBalance")
    val accountBalance: Double,
    @Json(name = "channelCount")
    val channelCount: Int,
    @Json(name = "dopOrderCount")
    val dopOrderCount: Any?,
    @Json(name = "earns")
    val earns: Double,
    @Json(name = "jdOrderCount")
    val jdOrderCount: Int,
    @Json(name = "stoOrderCount")
    val stoOrderCount: Int,
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "userName")
    val userName: String,
    @Json(name = "ytoOrderCount")
    val ytoOrderCount: Int
)