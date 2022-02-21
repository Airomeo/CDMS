package app.i.cdms.data.model


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookBody(
    val customerType: String?, // 通道类型不能为空
    val dateTime: String?,
    val deliveryType: String?,
    val goods: String?,
    val guaranteeValue: Int?,// 不太确定，null时无影响。会随另一个参数guaranteeValueAmount出现而出现，推测数值1是true。
    val guaranteeValueAmount: Int?,
    val packageCount: Int?,
    val qty: Int?,// 托寄物数量，申通专属参数，暂不明白与packageCount的区别。
    val pickUpEndTime: String?,
    val pickUpStartTime: String?,
    val receiveAddress: String?,
    val receiveAddressDetail: String?,
    val receiveCity: String?,
    val receiveDesc: String?,// This should be toRawAddress. But can be null after testing
    val receiveDistrict: String?,
    val receiveMobile: String?,
    val receiveName: String?,
    val receiveProvince: String?,
    val receiveProvinceCode: String?, // 寄收地【省】编码，必填
    val receiveTel: String?,
    val receiveValues: List<String>?,
    val remark: String?,
    val senderAddress: String?,
    val senderAddressDetail: String?,
    val senderCity: String?,
    val senderDesc: String?,// This should be fromRawAddress. But can be null after testing
    val senderDistrict: String?,
    val senderMobile: String?,
    val senderName: String?,
    val senderProvince: String?,
    val senderProvinceCode: String?, // 寄收地【省】编码，必填
    val senderTel: String?,
    val senderValues: List<String>?,
    val type: String?, // Not sure. Nullable after testing
    val vloumHeight: Int?,
    val vloumLong: Int?,
    val vloumWidth: Int?,
    val unitPrice: Int?, // 托寄物价格，申通专属参数
    val weight: Int?
) {
    constructor() : this(
        customerType = null,
        dateTime = null,
        deliveryType = null,
        goods = null,
        guaranteeValue = 1,
        guaranteeValueAmount = null,
        packageCount = null,
        qty = null,
        pickUpEndTime = null,
        pickUpStartTime = null,
        receiveAddress = null,
        receiveAddressDetail = null,
        receiveCity = null,
        receiveDesc = null,
        receiveDistrict = null,
        receiveMobile = null,
        receiveName = null,
        receiveProvince = null,
        receiveProvinceCode = null,
        receiveTel = null,
        receiveValues = null,
        remark = null,
        senderAddress = null,
        senderAddressDetail = null,
        senderCity = null,
        senderDesc = null,
        senderDistrict = null,
        senderMobile = null,
        senderName = null,
        senderProvince = null,
        senderProvinceCode = null,
        senderTel = null,
        senderValues = null,
        type = null,
        vloumHeight = null,
        vloumLong = null,
        vloumWidth = null,
        unitPrice = null,
        weight = null
    )
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
    "customerType": "personal",
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