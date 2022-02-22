package app.i.cdms.data.model.update


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Release(
    @Json(name = "enabled")
    val enabled: Boolean,
    @Json(name = "id")
    val id: Int,
    @Json(name = "is_external_build")
    val isExternalBuild: Boolean,
    @Json(name = "mandatory_update")
    val mandatoryUpdate: Boolean,
    @Json(name = "origin")
    val origin: String,
    @Json(name = "short_version")
    val shortVersion: String,
    @Json(name = "uploaded_at")
    val uploadedAt: String,
    @Json(name = "version")
    val version: String
)