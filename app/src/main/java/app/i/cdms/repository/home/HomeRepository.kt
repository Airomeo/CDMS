package app.i.cdms.repository.home

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.NoticeList
import app.i.cdms.data.source.local.UserPrefDataSource
import app.i.cdms.data.source.remote.home.HomeDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/10/22.
 */
class HomeRepository @Inject constructor(
    private val dataSource: HomeDataSource, private val userPrefDataSource: UserPrefDataSource
) : BaseRepository() {

    // in-memory cache of the loggedInUser object
    var user: MyInfo? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    private fun setLoggedInUser(loggedInUser: MyInfo) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun getMyInfo(): ApiResult<MyInfo>? {
        return executeResponse { dataSource.getMyInfo() }
    }

    suspend fun updateMyInfo(myInfo: MyInfo) {
        userPrefDataSource.updateMyInfo(myInfo)
    }

    suspend fun getNotice(): NoticeList? {
        return executeResponse { dataSource.getNotice() }
    }
}