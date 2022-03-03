package app.i.cdms.data.model


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Agent(
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "userName")
    val userName: String,
    @Json(name = "accountBalance")
    val accountBalance: String,
    @Json(name = "earns")
    val earns: String,
    @Json(name = "parentUserId")
    val parentUserId: Int,
    @Json(name = "parentUserName")
    val parentUserName: String,
    @Json(name = "childrenCount")
    val childrenCount: Int,
) : Parcelable