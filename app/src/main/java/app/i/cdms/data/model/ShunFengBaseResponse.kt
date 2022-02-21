package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ShunFengBaseResponse<out T : Any?>(
    @Json(name = "code")
    val code: Int,
    @Json(name = "detailMessage")
    val detailMessage: String?,
    @Json(name = "message")
    val message: String,
    @Json(name = "reqId")
    val reqId: Any?,
    @Json(name = "result")
    val result: T?
)