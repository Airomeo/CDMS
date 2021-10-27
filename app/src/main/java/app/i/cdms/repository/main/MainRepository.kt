package app.i.cdms.repository.main

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import app.i.cdms.data.source.remote.main.MainDataSource

/**
 * @author ZZY
 * 2021/10/22.
 */
class MainRepository(private val dataSource: MainDataSource) {

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

    suspend fun getMyInfo(token: Token): Result<ApiResult<MyInfo>> {
        // handle login
        val result = dataSource.getMyInfo(token)

        if (result is Result.Success) {
            result.data.data?.let { setLoggedInUser(it) }
        }

        return result
    }
}