package app.i.cdms.data.source.remote.main

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.Area
import app.i.cdms.data.model.CustomerChannelZone
import app.i.cdms.data.model.CustomerChannels
import app.i.cdms.data.model.YiDaBaseResponse
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that handles basic logic
 */
class MainDataSource @Inject constructor(private val service: ApiService) {
    suspend fun fetchCustomerChannels(customerType: String): Response<YiDaBaseResponse<CustomerChannels>> {
        return service.fetchCustomerChannels(customerType = customerType)
    }

    suspend fun getCustomerChannelDetail(customerId: Int): Response<YiDaBaseResponse<List<CustomerChannelZone>>> {
        return service.getCustomerChannelDetail(url = Constant.API_CUSTOMER_CHANNEL_DETAIL + "/" + customerId.toString())
    }

    suspend fun getAreaList(): Response<YiDaBaseResponse<List<Area>>> {
        return service.getAreaList()
    }

    suspend fun fetchRouters() = service.fetchRouters()
}