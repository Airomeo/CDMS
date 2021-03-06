package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Notice(
    @Json(name = "createBy")
    val createBy: String,
    @Json(name = "createTime")
    val createTime: String,
    @Json(name = "noticeContent")
    val noticeContent: String,
    @Json(name = "noticeId")
    val noticeId: Int,
    @Json(name = "noticeTitle")
    val noticeTitle: String,
    @Json(name = "noticeType")
    val noticeType: String,
    @Json(name = "params")
    val params: Any,
    @Json(name = "remark")
    val remark: String?,
    @Json(name = "searchValue")
    val searchValue: Any?,
    @Json(name = "status")
    val status: String,
    @Json(name = "updateBy")
    val updateBy: String,
    @Json(name = "updateTime")
    val updateTime: String
)

@JsonClass(generateAdapter = true)
data class NoticeList(
    @Json(name = "code")
    val code: Int,
    @Json(name = "msg")
    val msg: String,
    @Json(name = "rows")
    val notices: List<Notice>,
    @Json(name = "total")
    val total: Int
)