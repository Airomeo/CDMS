package app.i.cdms.repository

import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.SCFResult
import app.i.cdms.data.model.UserConfigResult
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import retrofit2.Response

/**
 * @author ZZY
 * 2022/1/6.
 */
open class BaseRepository {
    /**
     * repo 请求数据的公共方法，
     */
    suspend fun <T : Any> executeResponse(block: suspend () -> Response<T>): T? {
        kotlin.runCatching {
            EventBus.produceEvent(BaseEvent.Loading)
            block.invoke()
        }.onFailure {
            EventBus.produceEvent(BaseEvent.Error(it))
        }.onSuccess {
            EventBus.produceEvent(BaseEvent.Nothing)
            if (it.isSuccessful) {
                when (val body = it.body()) {
                    is ApiResult<*> -> {
                        when (body.code) {
                            200 -> {
                                return body as T
                            }
                            401 -> {
                                EventBus.produceEvent(BaseEvent.NeedLogin)
                            }
                            else -> {
                                EventBus.produceEvent(BaseEvent.Failed(body.toString()))
                            }
                        }
                    }
                    is UserConfigResult -> {

                    }
                    is SCFResult -> {

                    }
                    null -> {
                        // Response body is null.
                        // EventBus.produceEvent(BaseEvent.Failed("Response body is null."))
                    }
                    else -> {
                        EventBus.produceEvent(BaseEvent.Error(Exception("Other unhandled type")))
                    }
                }
            } else {
                EventBus.produceEvent(BaseEvent.Failed(it))
            }
        }
        return null
    }
}
