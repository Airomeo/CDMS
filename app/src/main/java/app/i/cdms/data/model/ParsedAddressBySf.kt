package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParsedAddressBySf(
    @Json(name = "hmtareaType")
    val hmtareaType: Int,
    @Json(name = "latitude")
    val latitude: Any?,
    @Json(name = "lontitude")
    val lontitude: Any?,
    @Json(name = "mobile")
    val mobile: String?,
    @Json(name = "originDestRegions")
    val originDestRegions: List<OriginDestRegion>,
    @Json(name = "personalName")
    val personalName: String?,
    @Json(name = "site")
    val site: String,
    @Json(name = "splitType")
    val splitType: Any?,
    @Json(name = "telephone")
    val telephone: String?
)