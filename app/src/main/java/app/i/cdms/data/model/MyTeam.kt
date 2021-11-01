package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyTeam(
    @Json(name = "countId")
    val countId: Any?,
    @Json(name = "current")
    val current: Int,
    @Json(name = "maxLimit")
    val maxLimit: Any?,
    @Json(name = "optimizeCountSql")
    val optimizeCountSql: Boolean,
    @Json(name = "orders")
    val orders: List<Any>,
    @Json(name = "pages")
    val pages: Int,
    @Json(name = "records")
    val records: List<Agent>,
    @Json(name = "searchCount")
    val searchCount: Boolean,
    @Json(name = "size")
    val size: Int,
    @Json(name = "total")
    val total: Int
)