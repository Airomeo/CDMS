package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SCFResult(
    @Json(name = "errorCode")
    val errorCode: Int,
    @Json(name = "errorMessage")
    val errorMessage: String,
    @Json(name = "requestId")
    val requestId: String?,
    @Json(name = "stackTrace")
    val stackTrace: String?,
    @Json(name = "statusCode")
    val statusCode: Int?
)


/*
{
    "errorCode": -1,
    "errorMessage": "user code exception caught",
    "requestId": "c8314069953e386e381add0f17a9f38d",
    "stackTrace": "Traceback (most recent call last):\n  File \"/var/user/index.py\", line 19, in main_handler\n    return set_commission(event)\n  File \"/var/user/index.py\", line 26, in set_commission\n    body = json.loads(event[\"body\"])\nKeyError: 'body'",
    "statusCode": 430
}*/

//{"errorCode": 500, "errorMsg": "fail"}
//{"errorCode": 200, "errorMsg": "success"}
//{"errorCode": 499, "errorMsg": "less body or authorization arguments"}