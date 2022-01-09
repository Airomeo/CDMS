package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CostPrice(
    @Json(name = "blocks")
    val blocks: List<Block>,
    @Json(name = "first")
    val first: String
)