package app.i.cdms.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class YunYangBaseResponse<out T : Any?>(
    @Json(name = "code")
    val code: String,
    @Json(name = "error")
    val error: Any?,
    @Json(name = "id")
    val id: String,
    @Json(name = "message")
    val message: String,
    @Json(name = "result")
    val result: T?
)

@JsonClass(generateAdapter = true)
data class YiDaBaseResponse<out T : Any?>(
    @Json(name = "code")
    val code: Int,
    @Json(name = "data")
    val `data`: T?,
    @Json(name = "msg")
    val msg: String
)

@JsonClass(generateAdapter = true)
data class ShunFengBaseResponse<out T : Any?>(
    @Json(name = "code")
    val code: Int,
    @Json(name = "detailMessage")
    val detailMessage: String?,
    @Json(name = "message")
    val message: String,
    @Json(name = "reqId")
    val reqId: Any?,
    @Json(name = "result")
    val result: T?
)

@JsonClass(generateAdapter = true)
data class SCFResult(
    @Json(name = "errorCode")
    val errorCode: Int, // -1
    @Json(name = "errorMessage")
    val errorMessage: String, // user code exception caught
    @Json(name = "requestId")
    val requestId: String?, // c8314069953e386e381add0f17a9f38d
    @Json(name = "stackTrace")
    val stackTrace: String?, // Traceback (most recent call last):  File "/var/user/index.py", line 19, in main_handler    return set_commission(event)  File "/var/user/index.py", line 26, in set_commission    body = json.loads(event["body"])KeyError: 'body'
    @Json(name = "statusCode")
    val statusCode: Int? // 430
)

//{"errorCode": 500, "errorMsg": "fail"}
//{"errorCode": 200, "errorMsg": "success"}
//{"errorCode": 499, "errorMsg": "less body or authorization arguments"}