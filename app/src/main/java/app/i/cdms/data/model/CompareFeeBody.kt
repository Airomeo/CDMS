package app.i.cdms.data.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CompareFeeBody(
    val customerType: String? = null, // 通道类型不能为空
    val receiveCity: String? = null,
    val receiveCityCode: String? = null,
    val senderCity: String? = null,
    val senderCityCode: String? = null,
    val weight: Int? = null
) {
    fun isFulfilled(): Boolean {
        return receiveCity != null && receiveCityCode != null && senderCity != null && senderCityCode != null && weight != null
    }
}