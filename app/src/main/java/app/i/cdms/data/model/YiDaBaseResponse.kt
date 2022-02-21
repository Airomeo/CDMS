package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YiDaBaseResponse<out T : Any?>(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: T?,
    @Json(name = "msg")
    val msg: String
)