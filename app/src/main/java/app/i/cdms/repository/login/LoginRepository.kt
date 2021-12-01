package app.i.cdms.repository.login

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import app.i.cdms.data.source.remote.login.LoginDataSource

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val dataSource: LoginDataSource) {

    suspend fun getCaptcha(): Result<ApiResult<CaptchaData>> {
        return dataSource.getCaptcha()
    }

    suspend fun login(
        username: String,
        password: String,
        captcha: Int,
        uuid: String
    ): Result<ApiResult<Token>> {
        // handle login
        return dataSource.login(username, password, captcha, uuid)
    }
}