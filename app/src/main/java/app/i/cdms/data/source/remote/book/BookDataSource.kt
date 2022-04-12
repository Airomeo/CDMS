package app.i.cdms.data.source.remote.book

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.*
import com.squareup.moshi.Moshi
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

    suspend fun fetchSmartPreOrderChannels(
        bookBody: BookBody
    ): Response<YiDaBaseResponse<ChannelsOf<PreOrderChannel>>> {
        val payload = BookBodyJsonAdapter(Moshi.Builder().build())
            .toJson(bookBody)
            .toRequestBody("application/json".toMediaType())

        return service.fetchSmartPreOrderChannels(payload = payload)
    }

    suspend fun submitOrder(
        bookBody: BookBody
    ): Response<YiDaBaseResponse<BookResult>> {
        val payload = BookBodyJsonAdapter(Moshi.Builder().build())
            .toJson(bookBody)
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