package app.i.cdms.repository.home

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Result
import app.i.cdms.data.source.local.UserPrefDataSource
import app.i.cdms.data.source.remote.home.HomeDataSource
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/10/22.
 */
class HomeRepository @Inject constructor(
    private val dataSource: HomeDataSource, private val userPrefDataSource: UserPrefDataSource
) {

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

    suspend fun getMyInfo(): Result<ApiResult<MyInfo>> {
        val result = dataSource.getMyInfo()
        if (result is Result.Success) {
            result.data.data?.let { setLoggedInUser(it) }
        }

        return result
    }

    suspend fun updateMyInfo(myInfo: MyInfo) {
        userPrefDataSource.updateMyInfo(myInfo)
    }
}