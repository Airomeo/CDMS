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
    suspend fun getCaptcha(
        @Url url: String = Constant.API_GET_CAPTCHA
    ): Response<YiDaBaseResponse<CaptchaData>>

    @POST
    suspend fun login(
        @Url url: String = Constant.API_LOGIN,
        @Body payload: RequestBody
    ): Response<YiDaBaseResponse<Token>>

    @GET
    suspend fun myInfo(@Url url: String = Constant.API_MY_INFO): Response<YiDaBaseResponse<MyInfo>>

    @POST
    suspend fun addChild(
        @Url url: String = Constant.API_ADD_CHILD,
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
    ): Response<MyTeam>

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
        @Query("customerType") customerType: String
    ): Response<YiDaBaseResponse<CustomerChannels>>

    @GET
    suspend fun getCustomerChannelDetail(
        @Url url: String
    ): Response<YiDaBaseResponse<List<CustomerChannelZone>>>

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
    suspend fun fetchRouters(
        @Url url: String = Constant.API_FETCH_ROUTERS
    ): Response<YiDaBaseResponse<List<Router>>>

    @GET
    suspend fun fetchChargeQrCode(
        @Url url: String = Constant.API_CHARGE,
        @Query("totalFee") amount: Int
    ): Response<YiDaBaseResponse<ChargeQrCode>>
}