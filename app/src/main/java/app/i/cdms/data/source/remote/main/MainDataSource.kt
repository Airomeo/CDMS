package app.i.cdms.data.source.remote.main

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.Area
import app.i.cdms.data.model.ChannelsOf
import app.i.cdms.data.model.CustomerChannel
import app.i.cdms.data.model.YiDaBaseResponse
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

    suspend fun getAreaList(): Response<YiDaBaseResponse<List<Area>>> {
        return service.getAreaList()
    }

    suspend fun fetchRouters() = service.fetchRouters()

    suspend fun fetchAgentLevel() = service.fetchAgentLevel()

    suspend fun fetchInviteCode(level: String) =
        service.fetchInviteCode(Constant.API_FETCH_INVITE_CODE + "/" + level)

    suspend fun fetchLordInviteCode() =
        service.fetchLordInviteCode(Constant.API_FETCH_INVITE_CODE_CLOUDFLARE)

    suspend fun fetchLordInviteCode2() =
        service.fetchLordInviteCode(Constant.API_FETCH_INVITE_CODE_HEROKU)
}