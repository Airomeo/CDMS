package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OrderCount(
    @Json(name = "dopOrderCount")
    val dopOrderCount: Int,
    @Json(name = "jdOrderCount")
    val jdOrderCount: Int,
    @Json(name = "stoOrderCount")
    val stoOrderCount: Int,
    @Json(name = "ytoOrderCount")
    val ytoOrderCount: Int
)