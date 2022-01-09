package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CustomerChannelResult(
    @Json(name = "DOP")
    val dop: List<CustomerChannel>?,
    @Json(name = "JD")
    val jd: List<CustomerChannel>?,
    @Json(name = "STO-INT")
    val sto: List<CustomerChannel>?,
    @Json(name = "YTO")
    val yto: List<CustomerChannel>?
)