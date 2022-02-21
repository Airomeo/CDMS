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
    suspend fun getCustomerChannel(customerType: String): Response<YiDaBaseResponse<CustomerChannelResult>> {
        return service.getCustomerChannel(customerType = customerType)
    }

    suspend fun getCustomerChannelDetail(customerId: Int): Response<YiDaBaseResponse<List<ChannelDetail>>> {
        return service.getCustomerChannelDetail(url = Constant.API_GET_CUSTOMER_CHANNELS + "/" + customerId.toString())
    }

    suspend fun checkUpdate(): Response<Release> {
        return service.checkUpdate()
    }

    suspend fun getAreaList(): Response<YiDaBaseResponse<List<Area>>> {
        return service.getAreaList()
    }
}