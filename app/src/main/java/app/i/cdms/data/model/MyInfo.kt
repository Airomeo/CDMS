package app.i.cdms.data.model


import app.i.cdms.data.model.Params
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MyInfo(
    @Json(name = "accountBalance")
    val accountBalance: Double,
    @Json(name = "createBy")
    val createBy: String,
    @Json(name = "createTime")
    val createTime: String,
    @Json(name = "earns")
    val earns: Double,
    @Json(name = "id")
    val id: Int,
    @Json(name = "params")
    val params: Params,
    @Json(name = "parentUserId")
    val parentUserId: Int,
    @Json(name = "payQrCode")
    val payQrCode: Any?,
    @Json(name = "sendAddress")
    val sendAddress: Any?,
    @Json(name = "updateBy")
    val updateBy: String,
    @Json(name = "updateTime")
    val updateTime: String?,
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "userName")
    val userName: String
)