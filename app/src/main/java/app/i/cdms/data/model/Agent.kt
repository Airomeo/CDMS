package app.i.cdms.data.model


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Agent(
    @Json(name = "accountBalance")
    val accountBalance: String, // 192.8
    @Json(name = "childrenCount")
    val childrenCount: Int, // 0
    @Json(name = "earns")
    val earns: String, // 0.0
    @Json(name = "levelType")
    val levelType: Int, // 30
    @Json(name = "parentUserId")
    val parentUserId: Int, // 2354
    @Json(name = "parentUserName")
    val parentUserName: String, // 朱朝阳
    @Json(name = "userId")
    val userId: Int, // 2363
    @Json(name = "userName")
    val userName: String, // 黄灿1
    @Json(name = "userType")
    val userType: String // N
) : Parcelable