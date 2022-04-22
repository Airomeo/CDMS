package app.i.cdms.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentLevel(
    @Json(name = "type")
    val type: String, // 30
    @Json(name = "desc")
    val desc: String, // 二级代理
)