package app.i.cdms.repository.register

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import app.i.cdms.data.source.remote.register.RegisterDataSource

/**
 * @author ZZY
 * 2021/10/26.
 */
class RegisterRepository(private val dataSource: RegisterDataSource) {
    suspend fun register(
        token: Token,
        username: String,
        password: String,
        phone: String
    ): Result<ApiResult<Any>> {
        return dataSource.register(token, username, password, phone)
    }
}