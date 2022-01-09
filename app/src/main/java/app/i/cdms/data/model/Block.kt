package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Block(
    @Json(name = "add")
    val add: String,
    @Json(name = "end")
    val end: String,
    @Json(name = "start")
    val start: String
)