package app.i.cdms.repository

import app.i.cdms.R
import app.i.cdms.data.model.ShunFengBaseResponse
import app.i.cdms.data.model.YiDaBaseResponse
import app.i.cdms.data.model.YunYangBaseResponse
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
            if ("504" in it.toString()) {
                // 云函数超时
                EventBus.produceEvent(BaseEvent.Toast(R.string.api_wait))
            } else {
                EventBus.produceEvent(BaseEvent.Error(it))
            }
        }.onSuccess {
            EventBus.produceEvent(BaseEvent.Nothing)
            if (it.isSuccessful) {
                when (val body = it.body()) {
                    is YiDaBaseResponse<*> -> {
                        when (body.code) {
                            200 -> {
                                return body
                            }
                            401 -> {
                                EventBus.produceEvent(BaseEvent.NeedLogin)
                            }
                            else -> {
                                EventBus.produceEvent(BaseEvent.Failed(body.msg))
                            }
                        }
                    }
                    is YunYangBaseResponse<*> -> {
                        when (body.code) {
                            "0" -> {
//                                {
//                                    "id": "16139358-c431-4597-b867-ce95ae07a114",
//                                    "code": "0",
//                                    "message": "解析失败：格式错误！请重新输入！",
//                                    "result": null,
//                                    "error": null
//                                }
                                EventBus.produceEvent(BaseEvent.Failed(body.message))
                            }
                            "1" -> {
//                                ParseAddressByJdResult
                                return body
                            }
                            "200" -> {
//                                {
//                                    "id": "d62a730e-98bd-41be-86a8-16b739e8961b",
//                                    "code": "200",
//                                    "message": "OK",
//                                    "result": null,
//                                    "error": null
//                                }
                                return body
                            }
                            else -> {
                                EventBus.produceEvent(BaseEvent.Failed(body.toString()))
                            }
                        }
                    }
                    is ShunFengBaseResponse<*> -> {
                        when (body.code) {
                            0 -> {
                                return body
                            }
                            else -> {
                                EventBus.produceEvent(BaseEvent.Failed(body.toString()))
                            }
                        }
                    }
//                    is UserConfigResult -> {
//                        return body
//                    }
//                    is SCFResult -> {
//                        return body
//                    }
//                    is NoticeList -> {
//                        return body
//                    }
                    null -> {
                        // Response body is null.
                        // EventBus.produceEvent(BaseEvent.Failed("Response body is null."))
                    }
                    else -> {
//                        EventBus.produceEvent(BaseEvent.Error(Exception("Other unhandled type")))
                        return body
                    }
                }
            } else {
                EventBus.produceEvent(BaseEvent.Failed(it))
            }
        }
        return null
    }
}
