package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookResult(
    @Json(name = "deliveryId")
    val deliveryId: String?,
    @Json(name = "orderNo")
    val orderNo: String,
    @Json(name = "printInfo")
    val printInfo: PrintInfo
)