package app.i.cdms.data.model


import app.i.cdms.utils.ChannelUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class Channel(
    val calcFeeType: String,// "calcFeeType": "profit" or "discount",
    val channelName: String,// "channelName": "申通22P全链路-总部TC渠道1区",
//    val channelZone: String,// "1区",
    val deliveryType: String,// "deliveryType": "STO-INT",
    val limitWeight: String,// "limitWeight": "50.0",
    val tariffZone: Any,// DiscountZone or ProfitZone
    // "1-1公斤,价格5.2续0; 2-3公斤,价格6.9续0; 4-50公斤,价格8.6续1.8;" or "折扣0.45,单笔加收0.5"
    val areaType: String?,// "areaType": "P",
    val backFeeType: String?,// "backFeeType": "whole",
    val lightGoods: String,// "lightGoods": "8000.0",
    var customerType: String? = null,// ky or kd or poizon
    var unfold: Boolean = false
)

@JsonClass(generateAdapter = true)
data class CustomerChannel(
    @Json(name = "customerId")// "customerId": 98,
    val customerId: Int,
    @Json(name = "areaType")// "areaType": "P",
    val areaType: String,
    @Json(name = "backFeeType")// "backFeeType": "whole",
    val backFeeType: String,
    @Json(name = "customerName")// "customerName": "极兔22P艾悦-外部渠道",
    val customerName: String,
    @Json(name = "customerType")// "customerType": "kd",
    val customerType: String,
    @Json(name = "deliveryBusiness")// "deliveryBusiness": "JT_BK",
    val deliveryBusiness: String,
    @Json(name = "deliveryType")// "deliveryType": "JT",
    val deliveryType: String,
    @Json(name = "lightGoods")// "lightGoods": "8000.0",
    val lightGoods: String,
    @Json(name = "limitWeight")// "limitWeight": "30.0",
    val limitWeight: String,
    @Json(name = "channelPrices")// "channelPrices": "1区:1-60公斤,价格9.6续2.1;\n2区:1-60公斤,价格9.6续3.4;\n3区:1-60公斤,价格9.6续4.1;\n4区:1-60公斤,价格9.6续5.2;\n5区:1-60公斤,价格11.5续8;"
    val channelPrices: String,
)

@JsonClass(generateAdapter = true)
data class CustomerChannelZone(
    @Json(name = "calcFeeType") // "calcFeeType": "profit",
    val calcFeeType: String,
    @Json(name = "channelId") // "channelId": 50,
    val channelId: Int,
    @Json(name = "channelName") // "channelName" : "极兔22P艾悦-外部渠道1区",
    val channelName: String,
    @Json(name = "price")// "price": "1-1公斤,价格5.1续0;2-3公斤,价格6.8续0;4-50公斤,价格8.6续1.8;",
    val price: String,
    @Json(name = "sort") // "sort": 1,
    val sort: Int,
    @Json(name = "srArea") // "srArea": null
    val srArea: List<String>?
)

@JsonClass(generateAdapter = true)
data class ChannelFees(
    @Json(name = "calcFeeType") // "calcFeeType": "profit" or "discount",
    val calcFeeType: String,
    @Json(name = "channelName") // "channelName": "申通22P全链路-总部TC渠道1区",
    val channelName: String,
    @Json(name = "deliveryType") // "deliveryType": "STO-INT",
    val deliveryType: String,
    @Json(name = "lightGoods") // "lightGoods": "8000.0",
    val lightGoods: String,
    @Json(name = "limitWeight") // "limitWeight": "50.0",
    val limitWeight: String,
    @Json(name = "preBjFee") // "preBjFee": null,
    val preBjFee: String?,
    @Json(name = "preOrderFee") // "preOrderFee": "5.3",
    val preOrderFee: String,
    @Json(name = "price") // "1-1公斤,价格5.2续0; 2-3公斤,价格6.9续0; 4-50公斤,价格8.6续1.8;" or "折扣0.45,单笔加收0.5"
    val price: String,
    @Json(name = "originalPrice") // "originalPrice": "1-0公斤,价格10续4;"
    val originalPrice: String,
    @Json(name = "originalFee") // "originalFee": "12"
    val originalFee: String,
    var customerType: String? = null
)

@JsonClass(generateAdapter = true)
data class ChannelsOf<out T>(
    @Json(name = "DOP")
    val dop: List<T>?,
    @Json(name = "JD")
    val jd: List<T>?,
    @Json(name = "STO-INT")
    val sto: List<T>?,
    @Json(name = "YTO")
    val yto: List<T>?,
    @Json(name = "JT")
    val jt: List<T>?,
    @Json(name = "SF")
    val sf: List<T>?,
    @Json(name = "ZTO")
    val zto: List<T>?,
    @Json(name = "YUND")
    val yd: List<T>?,
) {
    fun mapNotNull(): List<T> {
        val list = mutableListOf<T>()
        sto?.let { list.addAll(it) }
        yto?.let { list.addAll(it) }
        jd?.let { list.addAll(it) }
        dop?.let { list.addAll(it) }
        jt?.let { list.addAll(it) }
        sf?.let { list.addAll(it) }
        zto?.let { list.addAll(it) }
        yd?.let { list.addAll(it) }
        return list
    }
}

@JsonClass(generateAdapter = true)
data class PreOrderChannel(
    @Json(name = "calcFeeType")
    val calcFeeType: String, // profit
    @Json(name = "channelId")
    val channelId: String, // 56
    @Json(name = "channelName")
    val channelName: String, // 德邦22P艾悦-首重全国通渠道1区
    @Json(name = "deliveryType")
    val deliveryType: String, // DOP
    @Json(name = "isBest")
    val isBest: Boolean?, // true
    @Json(name = "lightGoods")
    val lightGoods: String, // 6000.0
    @Json(name = "limitWeight")
    val limitWeight: String, // 60.0
    @Json(name = "originalFee")
    val originalFee: String, // 10
    @Json(name = "originalPrice")
    val originalPrice: String, // 1-0公斤,价格10续2.14;
    @Json(name = "preBjFee")
    val preBjFee: String?, // 1
    @Json(name = "bjRuleStr")
    val bjRuleStr: String?, // "保额0至20000，费率0.004;保额20001至0，费率0.005;" or "不支持保价"
    @Json(name = "preOrderFee")
    val preOrderFee: String, // 10.6
    @Json(name = "price")
    val price: String // 1-60公斤,价格9.6续2.1;
) {
    val uiChannel = Channel(
        calcFeeType,
        if (preBjFee == null) {
            "¥$preOrderFee $channelName"
        } else {
            "¥$preOrderFee(保费¥$preBjFee) $channelName"
        },
        deliveryType,
        limitWeight,
        if (calcFeeType == "profit") ChannelUtil.parsePrice(price)
        else ChannelUtil.parseToDiscountZone(price)[0],
        null,
        null,
        lightGoods,
        null,
    )
}

@JsonClass(generateAdapter = true)
data class ChildChannelPrice(
    @Json(name = "calcFeeType")
    val calcFeeType: String?, // null
    @Json(name = "channelId")
    val channelId: Int?, // 148
    @Json(name = "createBy")
    val createBy: String?, // null
    @Json(name = "createTime")
    val createTime: String?, // null
    @Json(name = "id")
    val id: Int?, // 295164
    @Json(name = "levelType")
    val levelType: Int, // 20
    @Json(name = "oid")
    val oid: Any?, // null
    @Json(name = "params")
    val params: Any,
    @Json(name = "price")
    val price: String?, // 1-50=7.1+1.6
    @Json(name = "priceStr")
    val priceStr: String?, // 1-50公斤,价格7.1续1.6;
    @Json(name = "searchValue")
    val searchValue: Any?, // null
    @Json(name = "type")
    val type: Int?, // 1
    @Json(name = "updateBy")
    val updateBy: Any?, // null
    @Json(name = "updateTime")
    val updateTime: Any?, // null
    @Json(name = "userId")
    val userId: Int, // 2314
    @Json(name = "userName")
    val userName: String // 周国海1
)