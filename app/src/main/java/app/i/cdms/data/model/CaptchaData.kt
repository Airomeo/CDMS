package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CaptchaData(
    @Json(name = "captchaOnOff")
    val enabled: Boolean,
    @Json(name = "img")
    val imgBytes: String,
    @Json(name = "uuid")
    val uuid: String
)