package app.i.cdms.data

import android.util.Log
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.data.model.LoggedInUser
import app.i.cdms.data.model.Token

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    suspend fun getCaptcha(): Result<ApiResult<CaptchaData>> {
        // handle login
        val result = dataSource.getCaptcha()

        return result
    }

    suspend fun login(
        username: String,
        password: String,
        captcha: Int,
        uuid: String
    ): Result<ApiResult<Token>> {
        // handle login
        val result = dataSource.login(username, password, captcha, uuid)

        return result
    }

//    fun getInfo(token: String): Result<LoggedInUser> {
//        // handle login
//        val result = dataSource.getInfo(token)
//
//        if (result is Result.Success) {
//            setLoggedInUser(result.data)
//        }
//
//        return result
//    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
}