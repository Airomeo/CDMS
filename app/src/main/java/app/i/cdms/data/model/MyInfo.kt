package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyInfo(
    @Json(name = "accountBalance")
    val accountBalance: String, // 0.0
    @Json(name = "ancestorUserIds")
    val ancestorUserIds: String, // 1,2312,2354
    @Json(name = "createBy")
    val createBy: String?, // "admin" or null
    @Json(name = "createTime")
    val createTime: String, // 2022-04-30 20:21:45
    @Json(name = "earns")
    val earns: String, // 0.0
    @Json(name = "id")
    val id: Int, // 8697
    @Json(name = "levelType")
    val levelType: Int, // 30
    @Json(name = "params")
    val params: Any,
    @Json(name = "parentUserId")
    val parentUserId: Int, // 2354
    @Json(name = "searchValue")
    val searchValue: Any?, // null
    @Json(name = "updateBy")
    val updateBy: String?, // "徐子良" or null
    @Json(name = "updateTime")
    val updateTime: String, // 2022-04-30 20:21:45
    @Json(name = "userId")
    val userId: Int, // 8709
    @Json(name = "userName")
    val userName: String, // 小南315505
    @Json(name = "userType")
    val userType: String // N
)