package app.i.cdms.repository.login

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.data.model.Token
import app.i.cdms.data.source.remote.login.LoginDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class LoginRepository @Inject constructor(private val dataSource: LoginDataSource) :
    BaseRepository() {

    suspend fun getCaptcha(): ApiResult<CaptchaData>? {
        return executeResponse { dataSource.getCaptcha() }
    }

    suspend fun login(
        username: String,
        password: String,
        captcha: Int,
        uuid: String
    ): ApiResult<Token>? {
        // handle login
        return executeResponse { dataSource.login(username, password, captcha, uuid) }
    }
}
