package app.i.cdms.repository.main

import app.i.cdms.data.model.*
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

    suspend fun updateToken(token: Token) {
        userPrefDataSource.updateToken(token)
    }

    suspend fun getCustomerChannel(customerType: String): ApiResult<CustomerChannelResult>? {
        return executeResponse { dataSource.getCustomerChannel(customerType) }
    }

    suspend fun getCustomerChannelDetail(customerId: Int): ApiResult<List<ChannelDetail>>? {
        return executeResponse { dataSource.getCustomerChannelDetail(customerId) }
    }

    suspend fun checkUpdate(): Release? {
        return executeResponse { dataSource.checkUpdate() }
    }
}