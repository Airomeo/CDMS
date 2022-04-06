package app.i.cdms.data.source.remote.home

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.NoticeList
import app.i.cdms.data.model.YiDaBaseResponse
import retrofit2.Response
import javax.inject.Inject

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class HomeDataSource @Inject constructor(private val service: ApiService) {

    suspend fun getMyInfo(): Response<YiDaBaseResponse<MyInfo>> {
        return service.myInfo()
    }

    fun logout() {
        // TODO: revoke authentication
    }

    suspend fun getNotice(): Response<NoticeList> {
        return service.getNotice()
    }

    suspend fun fetchChargeQrCode(amount: Int) = service.fetchChargeQrCode(amount = amount)
}