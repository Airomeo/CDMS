package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouterMeta(
    @Json(name = "icon")
    val icon: String,
    @Json(name = "link")
    val link: Any?,
    @Json(name = "noCache")
    val noCache: Boolean,
    @Json(name = "title")
    val title: String
)