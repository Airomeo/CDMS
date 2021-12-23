package app.i.cdms.repository.register

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.source.remote.register.RegisterDataSource
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/10/26.
 */
class RegisterRepository @Inject constructor(private val dataSource: RegisterDataSource) {
    suspend fun register(
        username: String,
        password: String,
        phone: String
    ): Result<ApiResult<Any>> {
        return dataSource.register(username, password, phone)
    }
}