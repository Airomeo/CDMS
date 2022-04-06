package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChargeQrCode(
    @Json(name = "outTradeNo")
    val outTradeNo: String, // YD2022040618013948615389
    @Json(name = "qrCode")
    val qrCode: String // data:image/PNG;base64,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
)