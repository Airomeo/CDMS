package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Router(
    @Json(name = "alwaysShow")
    val alwaysShow: Boolean?,
    @Json(name = "children")
    val children: List<Router>?,
    @Json(name = "component")
    val component: String,
    @Json(name = "hidden")
    val hidden: Boolean,
    @Json(name = "meta")
    val meta: RouterMeta?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "path")
    val path: String,
    @Json(name = "redirect")
    val redirect: String?,
    @Json(name = "query")
    val query: String?
)