package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookBody(
    @Json(name = "channelId")
    val channelId: String? = null, // 35
    @Json(name = "customerType")
    val customerType: String? = "kd", // kd // 通道类型不能为空
    @Json(name = "dateTime")
    val dateTime: String? = null, // 2022-04-10 09:00-10:00
    @Json(name = "deliveryType")
    val deliveryType: String? = null, // STO-INT
    @Json(name = "goods")
    val goods: String? = null, // 日用品
    @Json(name = "guaranteeValueAmount")
    val guaranteeValueAmount: String? = null, // 111
    @Json(name = "packageCount")
    val packageCount: Int? = null, // 1 // 托寄物数量，申通专属参数，暂不明白与packageCount的区别。
    @Json(name = "pickUpEndTime")
    val pickUpEndTime: String? = null, // 2022-04-10 10:00:00
    @Json(name = "pickUpStartTime")
    val pickUpStartTime: String? = null, // 2022-04-10 09:00:00
    @Json(name = "qty")
    val qty: Int? = null, // 2
    @Json(name = "receiveAddress")
    val receiveAddress: String? = null, // 吴中区苏州工业园区生物纳米科技园C25栋
    @Json(name = "receiveCity")
    val receiveCity: String? = null, // 苏州市
    @Json(name = "receiveDistrict")
    val receiveDistrict: String? = null, // 姑苏区
    @Json(name = "receiveMobile")
    val receiveMobile: String? = null, // 13815951577
    @Json(name = "receiveName")
    val receiveName: String? = null, // 何先生
    @Json(name = "receiveProvince")
    val receiveProvince: String? = null, // 江苏省
    @Json(name = "receiveProvinceCode")
    val receiveProvinceCode: String? = null, // 320000 // 寄收地【省】编码，必填
    @Json(name = "receiveTel")
    val receiveTel: String? = null,
    @Json(name = "receiveValues")
    val receiveValues: List<String>? = null, // ["32000","321200","321202"]
    @Json(name = "senderAddress")
    val senderAddress: String? = null, // 吴中区苏州工业园区生物纳米科技园C25栋
    @Json(name = "senderCity")
    val senderCity: String? = null, // 苏州市
    @Json(name = "senderDistrict")
    val senderDistrict: String? = null, // 姑苏区
    @Json(name = "senderMobile")
    val senderMobile: String? = null, // 13815951577
    @Json(name = "senderName")
    val senderName: String? = null, // 何先生
    @Json(name = "senderProvince")
    val senderProvince: String? = null, // 江苏省
    @Json(name = "senderProvinceCode")
    val senderProvinceCode: String? = null, // 320000 // 寄收地【省】编码，必填
    @Json(name = "senderTel")
    val senderTel: String? = null,
    @Json(name = "senderValues")
    val senderValues: List<String>? = null, // ["32000","321200","321202"]
    @Json(name = "unitPrice")
    val unitPrice: Int? = null, // 1000 // 托寄物价格，申通专属参数。申通无保价费,提供对应货值证明,最高赔付金额2000元,单价请勿超过2000元
    @Json(name = "vloumHeight")
    val vloumHeight: String? = null, // 12
    @Json(name = "vloumLong")
    val vloumLong: String? = null, // 12
    @Json(name = "vloumWidth")
    val vloumWidth: String? = null, // 12
    @Json(name = "weight")
    val weight: Int? = null, // 1
    @Json(name = "remark")
    val remark: String? = null // 备注
) {
    val isReadyForPreOrder = goods != null
            && packageCount != null
            && receiveAddress != null
            && receiveCity != null
            && receiveDistrict != null
            && (receiveMobile ?: receiveTel) != null
            && receiveName != null
            && receiveProvince != null
            && receiveProvinceCode != null
            && receiveValues != null
            && senderAddress != null
            && senderCity != null
            && senderDistrict != null
            && (senderMobile ?: senderTel) != null
            && senderName != null
            && senderProvince != null
            && senderProvinceCode != null
            && senderValues != null
            && weight != null
    val isReadyForOrder = isReadyForPreOrder
            && customerType != null
            && channelId != null
            && deliveryType != null
            && qty != null
            && unitPrice != null

    //            && dateTime != null
//            && pickUpEndTime != null
//            && pickUpStartTime != null
//            && remark != null
//            && senderDesc != null
//            && senderAddressDetail != null
//            && receiveDesc != null
//            && receiveAddressDetail != null
//            && vloumHeight != null
//            && vloumLong != null
//            && vloumWidth != null
//            && guaranteeValue != null
//            && type != null
//            && guaranteeValueAmount != null
    fun getSenderNameAndPhoneOrNull(): String? {
        val phone = senderMobile ?: senderTel
        return if (senderName != null && phone != null) {
            "$senderName,$phone"
        } else {
            null
        }
    }

    fun getReceiverNameAndPhoneOrNull(): String? {
        val phone = receiveMobile ?: receiveTel
        return if (receiveName != null && phone != null) {
            "$receiveName,$phone"
        } else {
            null
        }
    }

    fun getSenderAddressOrNull() =
        if (senderProvince != null && senderCity != null && senderDistrict != null && senderAddress != null) {
            senderProvince + senderCity + senderDistrict + senderAddress
        } else {
            null
        }

    fun getReceiverAddressOrNull() =
        if (receiveProvince != null && receiveCity != null && receiveDistrict != null && receiveAddress != null) {
            receiveProvince + receiveCity + receiveDistrict + receiveAddress
        } else {
            null
        }
}

/*{
    "senderAddress": "干窑镇 三仙东路76号 东鼎名人府邸南苑2幢1单元1601 千山售后部   广东省深圳市龙华区观湖街道环观中路46号B栋三楼",
    "senderMobile": "17695686221",
    "senderTel": "13333333333",
    "senderName": "寄",
    "type": "receive",
    "senderValues": ["330000", "330400", "330421"],
    "senderDesc": "寄件人姓名：黄轩 \n寄件人电话：17695686221\n寄件人地址：浙江省嘉兴市嘉善县干窑镇\n详细地址: 三仙东路76号，东鼎名人府邸南苑2幢1单元1601\n收件人姓名：千山售后部\n收件人电话: 13502840881\n收件人地址：广东省深圳市龙华区观湖街道环观中路46号B栋三楼 ",
    "senderAddressDetail": null,
    "senderProvinceCode": "330000",
    "senderProvince": "浙江省",
    "senderCity": "嘉兴市",
    "senderDistrict": "嘉善县",
    "receiveAddressDetail": null,
    "receiveAddress": "龙华镇华翠路25号伟全化纤（惠州）有限公司B栋",
    "receiveMobile": "17728271436",
    "receiveTel": "13333333333",
    "receiveName": "收",
    "receiveValues": ["440000", "441300", "441322"],
    "receiveDesc": "收件人姓名：西遇电商退货仓\n收件人电话：17728271436\n收件人地址：广东省惠州市博罗县龙华镇华翠路25号伟全化纤（惠州）有限公司B栋\n",
    "receiveProvinceCode": "440000",
    "receiveProvince": "广东省",
    "receiveCity": "惠州市",
    "receiveDistrict": "博罗县",
    "deliveryType": "YTO",
    "goods": "1",
    "packageCount": 1,
    "weight": 1,
    "customerType": "kd",
    "guaranteeValueAmount": 111,
    "dateTime": "2022-02-14 15:00-16:00",
    "remark": "备注",
    "vloumLong": 11,
    "vloumWidth": 11,
    "vloumHeight": 11,
    "pickUpStartTime": "2022-02-14 15:00:00",
    "pickUpEndTime": "2022-02-14 16:00:00",
    "guaranteeValue": 1
}*/