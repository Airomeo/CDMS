package app.i.cdms.repository.agent

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import app.i.cdms.data.source.remote.agent.AgentDataSource

/**
 * @author ZZY
 * 2021/10/26.
 */
class AgentRepository(private val dataSource: AgentDataSource) {

    suspend fun withdrawal(token: Token, userId: Int): Result<ApiResult<Any>> {
        return dataSource.withdrawal(token, userId)
    }
}