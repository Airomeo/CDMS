package app.i.cdms.data.source.remote.book

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class BookDataSource @Inject constructor(private val service: ApiService) {

    suspend fun parseAddressByJd(rawAddress: String): Response<YunYangBaseResponse<ParsedAddressByJd>> {
        val payload = JSONObject()
            .put("address", rawAddress)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.parseAddressByJd(payload = payload)
    }

    suspend fun parseAddressBySf(rawAddress: String): Response<ShunFengBaseResponse<List<ParsedAddressBySf>>> {
        val payload = JSONObject()
            .put("address", rawAddress)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.parseAddressBySf(payload = payload)
    }

    suspend fun fetchPreOrderFee(
        bookBody: BookBody
    ): Response<YiDaBaseResponse<ChannelFees>> {
        val payload = JSONObject()
            .put("senderAddress", bookBody.senderAddress)
            .put("senderMobile", bookBody.senderMobile)
            .put("senderTel", bookBody.senderTel)
            .put("senderName", bookBody.senderName)
            .put("type", bookBody.type)
            .put("senderValues", bookBody.senderValues)
            .put("senderDesc", bookBody.senderDesc)
            .put("senderAddressDetail", bookBody.senderAddressDetail)
            .put("senderProvinceCode", bookBody.senderProvinceCode)
            .put("senderProvince", bookBody.senderProvince)
            .put("senderCity", bookBody.senderCity)
            .put("senderDistrict", bookBody.senderDistrict)
            .put("receiveAddressDetail", bookBody.receiveAddressDetail)
            .put("receiveAddress", bookBody.receiveAddress)
            .put("receiveMobile", bookBody.receiveMobile)
            .put("receiveTel", bookBody.receiveTel)
            .put("receiveName", bookBody.receiveName)
            .put("receiveValues", bookBody.receiveValues)
            .put("receiveDesc", bookBody.receiveDesc)
            .put("receiveProvinceCode", bookBody.receiveProvinceCode)
            .put("receiveProvince", bookBody.receiveProvince)
            .put("receiveCity", bookBody.receiveCity)
            .put("receiveDistrict", bookBody.receiveDistrict)
            .put("deliveryType", bookBody.deliveryType)
            .put("goods", bookBody.goods)
            .put("packageCount", bookBody.packageCount)
            .put("weight", bookBody.weight)
            .put("customerType", bookBody.customerType)
            .put("guaranteeValueAmount", bookBody.guaranteeValueAmount)
            .put("dateTime", bookBody.dateTime)
            .put("remark", bookBody.remark)
            .put("vloumLong", bookBody.vloumLong)
            .put("vloumWidth", bookBody.vloumWidth)
            .put("vloumHeight", bookBody.vloumHeight)
            .put("pickUpStartTime", bookBody.pickUpStartTime)
            .put("pickUpEndTime", bookBody.pickUpEndTime)
            .put("guaranteeValue", bookBody.guaranteeValue)
            .put("qty", bookBody.qty)
            .put("unitPrice", bookBody.unitPrice)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.fetchPreOrderFee(payload = payload)
    }

    suspend fun submitOrder(
        bookBody: BookBody
    ): Response<YiDaBaseResponse<BookResult>> {
        val payload = JSONObject()
            .put("senderAddress", bookBody.senderAddress)
            .put("senderMobile", bookBody.senderMobile)
            .put("senderTel", bookBody.senderTel)
            .put("senderName", bookBody.senderName)
            .put("type", bookBody.type)
            .put("senderValues", bookBody.senderValues)
            .put("senderDesc", bookBody.senderDesc)
            .put("senderAddressDetail", bookBody.senderAddressDetail)
            .put("senderProvinceCode", bookBody.senderProvinceCode)
            .put("senderProvince", bookBody.senderProvince)
            .put("senderCity", bookBody.senderCity)
            .put("senderDistrict", bookBody.senderDistrict)
            .put("receiveAddressDetail", bookBody.receiveAddressDetail)
            .put("receiveAddress", bookBody.receiveAddress)
            .put("receiveMobile", bookBody.receiveMobile)
            .put("receiveTel", bookBody.receiveTel)
            .put("receiveName", bookBody.receiveName)
            .put("receiveValues", bookBody.receiveValues)
            .put("receiveDesc", bookBody.receiveDesc)
            .put("receiveProvinceCode", bookBody.receiveProvinceCode)
            .put("receiveProvince", bookBody.receiveProvince)
            .put("receiveCity", bookBody.receiveCity)
            .put("receiveDistrict", bookBody.receiveDistrict)
            .put("deliveryType", bookBody.deliveryType)
            .put("goods", bookBody.goods)
            .put("packageCount", bookBody.packageCount)
            .put("weight", bookBody.weight)
            .put("customerType", bookBody.customerType)
            .put("guaranteeValueAmount", bookBody.guaranteeValueAmount)
            .put("dateTime", bookBody.dateTime)
            .put("remark", bookBody.remark)
            .put("vloumLong", bookBody.vloumLong)
            .put("vloumWidth", bookBody.vloumWidth)
            .put("vloumHeight", bookBody.vloumHeight)
            .put("pickUpStartTime", bookBody.pickUpStartTime)
            .put("pickUpEndTime", bookBody.pickUpEndTime)
            .put("guaranteeValue", bookBody.guaranteeValue)
            .put("qty", bookBody.qty)
            .put("unitPrice", bookBody.unitPrice)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.submitOrder(payload = payload)
    }

    suspend fun fetchCompareFee(compareFeeBody: CompareFeeBody): Response<YiDaBaseResponse<List<ChannelFees>>> {
        val payload = JSONObject()
            .put("receiveCityCode", compareFeeBody.receiveCityCode)
            .put("receiveCity", compareFeeBody.receiveCity)
            .put("senderCityCode", compareFeeBody.senderCityCode)
            .put("senderCity", compareFeeBody.senderCity)
            .put("weight", compareFeeBody.weight)
            .put("customerType", compareFeeBody.customerType)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return service.fetchCompareFee(payload = payload)
    }

    suspend fun getDeliveryId(
        orderNo: String,
        deliveryType: String
    ): Response<YiDaBaseResponse<String>> {
        val url = Constant.API_GET_DELIVERY_ID + "/" + deliveryType + "/" + orderNo
        return service.getDeliveryId(url = url)
    }
}