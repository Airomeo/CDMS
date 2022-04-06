package app.i.cdms.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentLevel(
    @Json(name = "createBy")
    val createBy: String,
    @Json(name = "createTime")
    val createTime: String,
    @Json(name = "flag")
    val flag: Boolean,
    @Json(name = "params")
    val params: Params,
    @Json(name = "postCode")
    val postCode: String,
    @Json(name = "postId")
    val postId: Int,
    @Json(name = "postName")
    val postName: String,
    @Json(name = "postSort")
    val postSort: String,
    @Json(name = "remark")
    val remark: Any?,
    @Json(name = "searchValue")
    val searchValue: Any?,
    @Json(name = "status")
    val status: String,
    @Json(name = "updateBy")
    val updateBy: String,
    @Json(name = "updateTime")
    val updateTime: String
)