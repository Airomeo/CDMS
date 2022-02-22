package app.i.cdms.data.model.update


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Owner(
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "name")
    val name: String
)