package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecordListResult(
    @Json(name = "code")
    val code: Int,
    @Json(name = "msg")
    val msg: String,
    @Json(name = "rows")
    val rows: List<RecordListRow>,
    @Json(name = "total")
    val total: Int
)