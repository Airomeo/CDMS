package app.i.cdms.repository.register

import app.i.cdms.data.model.YiDaBaseResponse
import app.i.cdms.data.source.remote.register.RegisterDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/10/26.
 */
class RegisterRepository @Inject constructor(private val dataSource: RegisterDataSource) :
    BaseRepository() {
    suspend fun register(
        username: String,
        password: String,
        phone: String
    ): YiDaBaseResponse<Any>? {
        return executeResponse { dataSource.register(username, password, phone) }
    }
}