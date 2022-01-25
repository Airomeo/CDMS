package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Release(
    @Json(name = "updateContent")
    val updateContent: String,
    @Json(name = "updateForce")
    val updateForce: Boolean,
    @Json(name = "updateUrl")
    val updateUrl: String,
    @Json(name = "versionCode")
    val versionCode: Int,
    @Json(name = "versionName")
    val versionName: String
)