package app.i.cdms.data.model.update


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DistributionGroup(
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "is_public")
    val isPublic: Boolean,
    @Json(name = "name")
    val name: String,
    @Json(name = "origin")
    val origin: String
)