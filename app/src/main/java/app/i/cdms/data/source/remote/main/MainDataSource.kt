package app.i.cdms.data.source.remote.main

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.Area
import app.i.cdms.data.model.ChannelDetail
import app.i.cdms.data.model.CustomerChannelResult
import app.i.cdms.data.model.YiDaBaseResponse
import app.i.cdms.data.model.update.Release
import app.i.cdms.data.model.update.ReleaseInfo
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

    suspend fun getReleases(): Response<List<Release>> {
        return service.getReleases()
    }

    suspend fun getReleaseInfo(id: Int): Response<ReleaseInfo> {
        return service.getReleaseInfo(url = Constant.API_GET_RELEASE_INFO + "/" + id.toString())
    }

    suspend fun getAreaList(): Response<YiDaBaseResponse<List<Area>>> {
        return service.getAreaList()
    }
}