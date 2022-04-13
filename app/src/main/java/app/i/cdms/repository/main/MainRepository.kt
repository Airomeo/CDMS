package app.i.cdms.repository.main

import app.i.cdms.data.model.CustomerChannelZone
import app.i.cdms.data.model.Token
import app.i.cdms.data.model.YiDaBaseResponse
import app.i.cdms.data.source.local.UserPrefDataSource
import app.i.cdms.data.source.remote.main.MainDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/10/22.
 */
class MainRepository @Inject constructor(
    private val dataSource: MainDataSource, private val userPrefDataSource: UserPrefDataSource
) : BaseRepository() {
    val token = userPrefDataSource.tokenFlow
    val areaListFlow = userPrefDataSource.areaListFlow
    val myInfo = userPrefDataSource.myInfoFlow

    suspend fun updateToken(token: Token?) {
        userPrefDataSource.updateToken(token)
    }

    suspend fun fetchCustomerChannels(
        customerType: String,
        scCode: String?,
        rcCode: String?
    ) = executeResponse { dataSource.fetchCustomerChannels(customerType, scCode, rcCode) }

    suspend fun getCustomerChannelDetail(customerId: Int): YiDaBaseResponse<List<CustomerChannelZone>>? {
        return executeResponse { dataSource.getCustomerChannelDetail(customerId) }
    }

    suspend fun getAndUpdateAreaList() {
        val result = executeResponse { dataSource.getAreaList() }
        result?.data?.let {
            userPrefDataSource.updateAreaList(it)
        }
    }

    suspend fun fetchRouters() = executeResponse { dataSource.fetchRouters() }
}