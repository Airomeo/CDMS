package app.i.cdms.api

import app.i.cdms.Constant
import app.i.cdms.data.model.*
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
    ): Response<ApiResult<CaptchaData>>

    @POST
    @Headers("Content-Type: application/json")
    suspend fun login(
        @Url url: String = Constant.API_LOGIN,
        @Body payload: Map<String, String>
    ): Response<ApiResult<Token>>

    @GET
    suspend fun myInfo(
        @Url url: String = Constant.API_MY_INFO,
        @Header("authorization") authorization: String?
    ): Response<ApiResult<MyInfo>>

    @POST
    @Headers("Content-Type: application/json")
    suspend fun addChild(
        @Url url: String = Constant.API_ADD_CHILD,
        @Header("authorization") authorization: String?,
        @Body payload: Map<String, String>
    ): Response<ApiResult<Any>>

    @GET
    suspend fun myTeam(
        @Url url: String = Constant.API_MY_TEAM,
        @Header("authorization") authorization: String?,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResult<MyTeam>>

    @GET
    suspend fun recordList(
        @Url url: String = Constant.API_RECORD_LIST,
        @Header("authorization") authorization: String?,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
        @Query("recordType") recordType: Int
    ): Response<RecordListResult>

    @DELETE
    suspend fun removePrice(
        @Url url: String = Constant.API_REMOVE_PRICE,
        @Header("authorization") authorization: String?,
        @Query("channelId") channelId: Int
    ): Response<ApiResult<Any>>

    @PUT
    suspend fun editPrice(
        @Url url: String = Constant.API_EDIT_PRICE,
        @Header("authorization") authorization: String?,
        @Query("userId") userId: Int
    ): Response<ApiResult<Any>>

//        val payload: Map<String, String> = mapOf(
//            "addPrice" to addPrice,
//            "channelId" to channelId,
//            "firstPrice" to firstPrice,
//            "firstWeight" to firstWeight,
//            "id" to id,
//            "limitAddPrice" to limitAddPrice,
//            "limitFirstPrice" to limitFirstPrice,
//            "userId" to userId
//        )

    @POST
    suspend fun addPrice(
        @Url url: String = Constant.API_ADD_PRICE,
        @Header("authorization") authorization: String?,
        @Query("userId") userId: Int
    ): Response<ApiResult<Any>>

//        val payload: Map<String, String> = mapOf(
//            "addPrice" to addPrice,
//            "channelId" to channelId,
//            "firstPrice" to firstPrice,
//            "firstWeight" to firstWeight,
//            "limitAddPrice" to limitAddPrice,
//            "limitFirstPrice" to limitFirstPrice,
//            "userId" to userId
//        )

    @GET
    suspend fun clearAccount(
        @Url url: String = Constant.API_CLEAR_ACCOUNT,
        @Header("authorization") authorization: String?,
        @Query("userId") userId: Int
    ): Response<ApiResult<Any>>

    @GET
    suspend fun transfer(
        @Url url: String = Constant.API_TRANSFER,
        @Header("authorization") authorization: String?,
        @Query("toUserId") toUserId: Int,
        @Query("userName") userName: String,
        @Query("remark") remark: String?,
        @Query("recordType") recordType: String,
        @Query("changeAmount") changeAmount: Float
    ): Response<ApiResult<Any>>

    @GET
    suspend fun myPrice(
        @Url url: String = Constant.API_MY_PRICE,
        @Header("authorization") authorization: String?
    ): Response<ApiResult<ArrayList<MyPriceItem>>>
}