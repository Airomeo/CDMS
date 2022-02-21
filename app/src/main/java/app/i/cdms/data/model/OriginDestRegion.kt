package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OriginDestRegion(
    @Json(name = "availableAsDestination")
    val availableAsDestination: Boolean,
    @Json(name = "availableAsOrigin")
    val availableAsOrigin: Boolean,
    @Json(name = "code")
    val code: String,
    @Json(name = "countryCode")
    val countryCode: String,
    @Json(name = "distId")
    val distId: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "lang")
    val lang: String,
    @Json(name = "level")
    val level: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "parentCode")
    val parentCode: String,
    @Json(name = "parentId")
    val parentId: String,
    @Json(name = "rateCode")
    val rateCode: String
)