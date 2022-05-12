package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PageOf<out T>(
    @Json(name = "msg")
    val msg: String?,
    @Json(name = "rows")
    val rows: List<T>?,
    @Json(name = "code")
    val code: Int,
    @Json(name = "total")
    val total: Int?
)