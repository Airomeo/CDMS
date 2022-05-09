package app.i.cdms.repository

import app.i.cdms.data.model.Token
import app.i.cdms.data.model.YiDaBaseResponse
import app.i.cdms.data.source.remote.AuthDataSource
import javax.inject.Inject

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
class AuthRepository @Inject constructor(private val dataSource: AuthDataSource) :
    BaseRepository() {

    suspend fun fetchLoginCaptcha() = executeResponse { dataSource.fetchLoginCaptcha() }
    suspend fun fetchRegisterCaptcha(phone: String) =
        executeResponse { dataSource.fetchRegisterCaptcha(phone) }

    suspend fun login(
        username: String,
        password: String,
        captcha: Int,
        uuid: String
    ): YiDaBaseResponse<Token>? {
        // handle login
        return executeResponse { dataSource.login(username, password, captcha, uuid) }
    }

    suspend fun register(
        username: String,
        password: String,
        phone: String,
        phoneCaptcha: String,
        inviteCode: String
    ) = executeResponse { dataSource.register(username, password, phone, phoneCaptcha, inviteCode) }

    suspend fun retrievePassword(phone: String) =
        executeResponse { dataSource.retrievePassword(phone) }

    suspend fun updatePassword(old: String, new: String) =
        executeResponse { dataSource.updatePassword(old, new) }
}
