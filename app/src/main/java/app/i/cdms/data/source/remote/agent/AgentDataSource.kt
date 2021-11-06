package app.i.cdms.data.source.remote.agent

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import java.io.IOException

/**
 * @author ZZY
 * 2021/11/6.
 */
class AgentDataSource(private val service: ApiService) {

    suspend fun withdrawal(
        token: Token,
        userId: Int
    ): Result<ApiResult<Any>> {
        return try {
            val response = service.clearAccount(authorization = token.token, userId = userId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception("Error withdrawal"))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error withdrawal" + e.localizedMessage, e))
        }
    }
}
