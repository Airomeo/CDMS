package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Area(
    @Json(name = "children")
    val children: List<Area>?,
    @Json(name = "label")
    val label: String,
    @Json(name = "value")
    val value: String
)