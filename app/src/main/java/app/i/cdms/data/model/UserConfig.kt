package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserConfig(
    @Json(name = "add_commission")
    val addCommission: Float,
    @Json(name = "first_commission")
    val firstCommission: Float,
    @Json(name = "first_weight")
    val firstWeight: Float,
    @Json(name = "_id")
    val id: String,
    @Json(name = "limit_add_commission")
    val limitAddCommission: Float,
    @Json(name = "limit_first_commission")
    val limitFirstCommission: Float,
    @Json(name = "super_user_id")
    val superUserId: Int,
    @Json(name = "user_id")
    val userId: Int
)