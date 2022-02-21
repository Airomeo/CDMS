package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YunYangBaseResponse<out T : Any?>(
    @Json(name = "code")
    val code: String,
    @Json(name = "error")
    val error: Any?,
    @Json(name = "id")
    val id: String,
    @Json(name = "message")
    val message: String,
    @Json(name = "result")
    val result: T?
)