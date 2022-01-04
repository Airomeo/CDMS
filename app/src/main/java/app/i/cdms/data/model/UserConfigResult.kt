package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserConfigResult(
    @Json(name = "data")
    val `data`: List<UserConfig>,
    @Json(name = "requestId")
    val requestId: String
)