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
    suspend fun fetchCustomerChannels(
        customerType: String,
        scCode: String?,
        rcCode: String?
    ): Response<YiDaBaseResponse<ChannelsOf<CustomerChannel>>> {
        return service.fetchCustomerChannels(
            customerType = customerType,
            scCode = scCode,
            rcCode = rcCode
        )
    }

    suspend fun getCustomerChannelDetail(customerId: Int): Response<YiDaBaseResponse<List<CustomerChannelZone>>> {
        return service.getCustomerChannelDetail(url = Constant.API_CUSTOMER_CHANNEL_DETAIL + "/" + customerId.toString())
    }

    suspend fun getAreaList(): Response<YiDaBaseResponse<List<Area>>> {
        return service.getAreaList()
    }

    suspend fun fetchRouters() = service.fetchRouters()

    suspend fun fetchAgentLevel() = service.fetchAgentLevel()

    suspend fun fetchInviteCode(level: String) =
        service.fetchInviteCode(Constant.API_FETCH_INVITE_CODE + "/" + level)

    suspend fun fetchLordInviteCode() =
        service.fetchLordInviteCode(Constant.API_HEROKU_FETCH_INVITE_CODE)
}