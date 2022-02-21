package app.i.cdms.data.source.remote.main

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.*
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that handles basic logic
 */
class MainDataSource @Inject constructor(private val service: ApiService) {
    suspend fun getCustomerChannel(customerType: String): Response<ApiResult<CustomerChannelResult>> {
        return service.getCustomerChannel(customerType = customerType)
    }

    suspend fun getCustomerChannelDetail(customerId: Int): Response<ApiResult<List<ChannelDetail>>> {
        return service.getCustomerChannelDetail(url = Constant.API_GET_CUSTOMER_CHANNELS + "/" + customerId.toString())
    }

    suspend fun checkUpdate(): Response<Release> {
        return service.checkUpdate()
    }

    suspend fun getAreaList(): Response<ApiResult<List<Area>>> {
        return service.getAreaList()
    }
}