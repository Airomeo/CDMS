package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class PrintInfo(
    @Json(name = "shortAddress")
    val shortAddress: String?,
    @Json(name = "pdfUrl")
    val pdfUrl: String?
)