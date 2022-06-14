package app.i.cdms.api

import app.i.cdms.Constant
import app.i.cdms.data.model.*
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @author ZZY
 * 2021/10/18.
 */
interface ApiService {
//    @GET("?key=$PIXABAY_KEY")
//    suspend fun getPixabayPictures(
//        @Query("q") query: String,
//        @Query("page") index: Int
//    ): Response<Pic>

//    @GET
//    suspend fun getUnsplashPictures(
//        @Url url: String,
//        @Query("query") query: String,
//        @Query("page") page: Int
//    ): Response<UnsplashResponse>

//    @GET
//    suspend fun getVideos(
//        @Url url: String,
//        @Query("playlistId") playlistId: String
//    ): Response<VideoResponse>

    @GET
    suspend fun fetchLoginCaptcha(
        @Url url: String = Constant.API_LOGIN_CAPTCHA
    ): Response<YiDaBaseResponse<CaptchaData>>

    @PUT
    suspend fun fetchRegisterCaptcha(
        @Url url: String
    ): Response<YiDaBaseResponse<Any?>>

    @POST
    suspend fun login(
        @Url url: String = Constant.API_LOGIN,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<Token>>

    @PUT
    suspend fun retrievePassword(@Url url: String): Response<YiDaBaseResponse<Any>>

    @PUT
    suspend fun updatePassword(
        @Url url: String = Constant.API_UPDATE_PASSWORD,
        @Query("oldPassword") oldPassword: String,
        @Query("newPassword") newPassword: String,
    ): Response<YiDaBaseResponse<Any>>

    @GET
    suspend fun myInfo(@Url url: String = Constant.API_MY_INFO): Response<YiDaBaseResponse<MyInfo>>

    @POST
    suspend fun register(
        @Url url: String = Constant.API_REGISTER,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<Any>>

    @GET
    suspend fun myTeam(
        @Url url: String = Constant.API_MY_TEAM,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
        @Query("userName") userName: String?,
        @Query("parentUserId") parentUserId: Int?,
        @Query("than") than: String?,
        @Query("balance") balance: String?,
    ): Response<PageOf<Agent>>

    @GET
    suspend fun clearAccount(
        @Url url: String = Constant.API_CLEAR_ACCOUNT,
        @Query("userId") userId: Int
    ): Response<YiDaBaseResponse<Any>>

    @PUT
    suspend fun transfer(
        @Url url: String = Constant.API_TRANSFER,
        @Query("toUserId") toUserId: Int,
        @Query("userName") userName: String,
        @Query("remark") remark: String?,
        @Query("recordType") recordType: String,
        @Query("changeAmount") changeAmount: Float
    ): Response<YiDaBaseResponse<Any>>

    @GET
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Accept-Language: en-US,en;q=0.9,zh-CN;q=0.8,zh;q=0.7"
    )
    suspend fun getNotice(
        @Url url: String = Constant.API_NOTICE
    ): Response<NoticeList>

    @GET
    suspend fun getOrderCount(
        @Url url: String = Constant.API_ORDER_COUNT,
        @Query("userId") userId: Int
    ): Response<YiDaBaseResponse<OrderCount>>

    @GET
    suspend fun fetchCustomerChannels(
        @Url url: String = Constant.API_CUSTOMER_CHANNEL,
        @Query("customerType") customerType: String,
        @Query("scCode") scCode: String?, // 寄
        @Query("rcCode") rcCode: String? // 收
    ): Response<YiDaBaseResponse<ChannelsOf<CustomerChannel>>>

    @GET
    suspend fun getCustomerChannelPrice(@Url url: String): Response<YiDaBaseResponse<List<CustomerChannelZone>>>

    @GET
    suspend fun fetchBindUserPrice(
        @Url url: String = Constant.API_GET_USER_PRICE,
        @Query("channelId") channelId: Int,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<PageOf<ChildChannelPrice>>

    @GET
    suspend fun fetchUserPrice(
        @Url url: String = Constant.API_GET_USER_PRICE_BY_BIND,
        @Query("channelId") channelId: Int,
        @Query("isBind") isBind: Boolean,
    ): Response<YiDaBaseResponse<List<ChildChannelPrice>>>

    @POST
    suspend fun addUserPrice(
        @Url url: String = Constant.API_ADD_USER_PRICE,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<Any>>

    @PUT
    suspend fun updateUserPrice(
        @Url url: String = Constant.API_UPDATE_USER_PRICE,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<Any>>

    @DELETE
    suspend fun deleteChildPrice(@Url url: String): Response<YiDaBaseResponse<Any>>

    @GET
    suspend fun getAreaList(
        @Url url: String = Constant.API_GET_AREA
    ): Response<YiDaBaseResponse<List<Area>>>

    @POST
    suspend fun parseAddressByJd(
        @Url url: String = Constant.API_PARSE_ADDRESS_BY_JD,
        @Body payload: RequestBody
    ): Response<YunYangBaseResponse<ParsedAddressByJd>>

    @POST
    suspend fun fetchSmartPreOrderChannels(
        @Url url: String = Constant.API_SMART_PRE_ORDER,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<ChannelsOf<PreOrderChannel>>>

    @POST
    suspend fun submitOrder(
        @Url url: String = Constant.API_SUBMIT_ORDER,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<BookResult>>

    @POST
    suspend fun fetchCompareFee(
        @Url url: String = Constant.API_GET_COMPARE_FEE,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<List<ChannelFees>>>

    @POST
    suspend fun parseAddressBySf(
        @Url url: String = Constant.API_PARSE_ADDRESS_BY_SF,
        @Query("lang") lang: String = "sc",
        @Query("region") region: String = "cn",
        @Query("translate") translate: String = "sc",
        @Body payload: RequestBody
    ): Response<ShunFengBaseResponse<List<ParsedAddressBySf>>>

    @GET
    suspend fun getDeliveryId(
        @Url url: String
    ): Response<YiDaBaseResponse<String>>

    @GET
    suspend fun fetchAgentLevel(
        @Url url: String = Constant.API_FETCH_POST_CODE
    ): Response<YiDaBaseResponse<List<AgentLevel>>>

    @PUT
    suspend fun fetchInviteCode(@Url url: String): Response<YiDaBaseResponse<String>>

    @GET
    suspend fun fetchLordInviteCode(@Url url: String): Response<YiDaBaseResponse<String>>

    @GET
    suspend fun fetchRouters(
        @Url url: String = Constant.API_FETCH_ROUTERS
    ): Response<YiDaBaseResponse<List<Router>>>

    @GET
    suspend fun fetchChargeQrCode(
        @Url url: String = Constant.API_CHARGE,
        @Query("totalFee") amount: Int
    ): Response<YiDaBaseResponse<ChargeQrCode>>

    @GET
    suspend fun fetchOrderList(
        @Url url: String,
        @Query("deliveryType") deliveryType: String, // YTO
        @Query("customerType") customerType: String, // kd
        @Query("pageNum") pageNum: Int?,
        @Query("pageSize") pageSize: Int?,
        @Query("params[beginTime]") beginTime: String,
        @Query("params[endTime]") endTime: String, // 2022-05-12 00:00:00 2022-05-19%2000%3A00%3A00
        @Query("weightState") weightState: Int?, // 1
        @Query("goods") goods: String?, // 日用百货
        @Query("orderStatus") orderStatus: Int?, // 补差状态 1
        @Query("senderSearch") senderSearch: Int?, // 寄件人姓名/电话/手机
        @Query("receiveSearch") receiveSearch: Int?, // 收件人姓名/电话/手机
        @Query("userId") userId: Int?, // 下单用户id 2363
        @Query("thirdNo") thirdNo: String?, // 第三方订单号
        @Query("orderNo") orderNo: String?, // 订单号
        @Query("deliveryIds") deliveryIds: String?, // 运单编号中/英文逗号隔开
    ): Response<PageOf<RawOrder>>

    @POST
    suspend fun updateOrderWeightState(
        @Url url: String = Constant.API_UPDATE_ORDER_WEIGHT_STATE,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<Boolean>>

    @GET
    suspend fun batchCancelOrder(@Url url: String): Response<YiDaBaseResponse<List<CancelOrderResult>>>

}