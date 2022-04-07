package app.i.cdms.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentLevel(
    @Json(name = "createBy")
    val createBy: String, // admin
    @Json(name = "createTime")
    val createTime: String, // 2022-03-02 23:57:28
    @Json(name = "flag")
    val flag: Boolean, // false
    @Json(name = "params")
    val params: Any,
    @Json(name = "postCode")
    val postCode: String, // level_3
    @Json(name = "postId")
    val postId: Int, // 4
    @Json(name = "postName")
    val postName: String, // 二级代理
    @Json(name = "postSort")
    val postSort: String, // 4
    @Json(name = "remark")
    val remark: Any?, // null
    @Json(name = "searchValue")
    val searchValue: Any?, // null
    @Json(name = "status")
    val status: String, // 0
    @Json(name = "updateBy")
    val updateBy: String, // admin
    @Json(name = "updateTime")
    val updateTime: String // 2022-03-03 01:26:49
)