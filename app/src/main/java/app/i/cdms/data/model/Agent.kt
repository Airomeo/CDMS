package app.i.cdms.data.model


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Agent(
    @Json(name = "accountBalance")
    val accountBalance: Float,
    @Json(name = "channelCount")
    val channelCount: Int?,
    @Json(name = "earns")
    val earns: Float,
    @Json(name = "userId")
    val userId: Int,
    @Json(name = "sonUserCount")
    val sonUserCount: Int?,
    @Json(name = "userName")
    val userName: String
) : Parcelable