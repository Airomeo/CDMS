package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ParsedAddressBySf(
    @Json(name = "hmtareaType")
    val hmtareaType: Int,
    @Json(name = "latitude")
    val latitude: Any?,
    @Json(name = "lontitude")
    val lontitude: Any?,
    @Json(name = "mobile")
    val mobile: String?,
    @Json(name = "originDestRegions")
    val originDestRegions: List<OriginDestRegion>,
    @Json(name = "personalName")
    val personalName: String?,
    @Json(name = "site")
    val site: String,
    @Json(name = "splitType")
    val splitType: Any?,
    @Json(name = "telephone")
    val telephone: String?
)

@JsonClass(generateAdapter = true)
data class OriginDestRegion(
    @Json(name = "availableAsDestination")
    val availableAsDestination: Boolean,
    @Json(name = "availableAsOrigin")
    val availableAsOrigin: Boolean,
    @Json(name = "code")
    val code: String,
    @Json(name = "countryCode")
    val countryCode: String,
    @Json(name = "distId")
    val distId: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "lang")
    val lang: String,
    @Json(name = "level")
    val level: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "parentCode")
    val parentCode: String,
    @Json(name = "parentId")
    val parentId: String,
    @Json(name = "rateCode")
    val rateCode: String
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