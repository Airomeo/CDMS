package app.i.cdms.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BookResult(
    @Json(name = "deliveryId")//"deliveryId": null,
    val deliveryId: String?,
    @Json(name = "orderNo")//"orderNo": "YT20220405022431136781",
    val orderNo: String,
    @Json(name = "upOrderId")//"upOrderId": "17829835",
    val upOrderId: String?,
    @Json(name = "taskId")//"taskId": "5E61455013540455410D30FF9725EBF2",
    val taskId: String?,
    @Json(name = "printInfo")//"printInfo": null
    val printInfo: PrintInfo?
)
//"yto": {
//    "deliveryId": null,
//    "orderNo": "YT20220405022431136781",
//    "upOrderId": "17829835",
//    "taskId": "5E61455013540455410D30FF9725EBF2",
//    "printInfo": null
//}
//"dpk": {
//    "deliveryId": "DPK202058224204",
//    "orderNo": "DB20220405022721559905",
//    "upOrderId": null,
//    "taskId": null,
//    "printInfo": null
//}
//"jt": {
//    "deliveryId": null,
//    "orderNo": "JT20220405022904865492",
//    "upOrderId": "17829840",
//    "taskId": "FF6DBAF477CA2BAA96A21318F1FDA773",
//    "printInfo": null
//}