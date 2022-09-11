package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParsedAddressBySf(
    @Json(name = "hMTAreaType")
    val hMTAreaType: Int, // 2
    @Json(name = "mobile")
    val mobile: String, // 18179238332
    @Json(name = "originDestRegions")
    val originDestRegions: List<OriginDestRegion>,
    @Json(name = "personalName")
    val personalName: String, // 付玲
    @Json(name = "site")
    val site: String, // 向阳街道办事处(九江经济技术开发区) 柴桑春天三区 5-2栋 204室
    @Json(name = "telephone")
    val telephone: String
)

@JsonClass(generateAdapter = true)
data class OriginDestRegion(
    @Json(name = "availableAsDestination")
    val availableAsDestination: Boolean, // true
    @Json(name = "availableAsOrigin")
    val availableAsOrigin: Boolean, // true
    @Json(name = "code")
    val code: String, // A360000000
    @Json(name = "countryCode")
    val countryCode: String, // A000086000
    @Json(name = "distId")
    val distId: String, // 43
    @Json(name = "id")
    val id: String, // 43-SC
    @Json(name = "lang")
    val lang: String, // SC
    @Json(name = "level")
    val level: Int, // 2
    @Json(name = "name")
    val name: String, // 江西省
    @Json(name = "parentCode")
    val parentCode: String, // A000086000
    @Json(name = "parentId")
    val parentId: String, // 11
    @Json(name = "rateCode")
    val rateCode: String // 360
)

@JsonClass(generateAdapter = true)
data class ParsedAddressByJd(
    @Json(name = "address")
    val address: String,
    @Json(name = "addressType")
    val addressType: Any?,
    @Json(name = "city")
    val city: String,
    @Json(name = "cityId")
    val cityId: Int,
    @Json(name = "company")
    val company: Any?,
    @Json(name = "contact")
    val contact: String?,
    @Json(name = "contactPinyin")
    val contactPinyin: Any?,
    @Json(name = "county")
    val county: String,
    @Json(name = "countyId")
    val countyId: Int,
    @Json(name = "createOperator")
    val createOperator: Any?,
    @Json(name = "createTime")
    val createTime: Any?,
    @Json(name = "defaultYn")
    val defaultYn: Any?,
    @Json(name = "defaultYnName")
    val defaultYnName: Any?,
    @Json(name = "email")
    val email: Any?,
    @Json(name = "gbAddress")
    val gbAddress: Boolean,
    @Json(name = "id")
    val id: Any?,
    @Json(name = "isCommonAddress")
    val isCommonAddress: Any?,
    @Json(name = "isCommonAddressName")
    val isCommonAddressName: Any?,
    @Json(name = "isOperated")
    val isOperated: Any?,
    @Json(name = "josPin")
    val josPin: Any?,
    @Json(name = "latitude")
    val latitude: Any?,
    @Json(name = "longitude")
    val longitude: Any?,
    @Json(name = "mobile")
    val mobile: String?,
    @Json(name = "operationTime")
    val operationTime: Any?,
    @Json(name = "ownSign")
    val ownSign: Any?,
    @Json(name = "postcode")
    val postcode: Any?,
    @Json(name = "province")
    val province: String,
    @Json(name = "provinceId")
    val provinceId: Int,
    @Json(name = "recCompany")
    val recCompany: Any?,
    @Json(name = "refDropShippingOrderNo")
    val refDropShippingOrderNo: Any?,
    @Json(name = "shopCode")
    val shopCode: Any?,
    @Json(name = "shopType")
    val shopType: Any?,
    @Json(name = "sourceType")
    val sourceType: Any?,
    @Json(name = "sourceVendorCode")
    val sourceVendorCode: Any?,
    @Json(name = "sourceVendorName")
    val sourceVendorName: Any?,
    @Json(name = "standardType")
    val standardType: Int,
    @Json(name = "syncSource")
    val syncSource: Any?,
    @Json(name = "telphone")
    val telphone: Any?,
    @Json(name = "town")
    val town: Any?,
    @Json(name = "townId")
    val townId: String?,
    @Json(name = "updateOperator")
    val updateOperator: Any?,
    @Json(name = "updateTime")
    val updateTime: Any?,
    @Json(name = "vendorId")
    val vendorId: Any?,
    @Json(name = "warehouseCode")
    val warehouseCode: Any?,
    @Json(name = "yn")
    val yn: Any?
)