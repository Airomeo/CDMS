package app.i.cdms.data.source.remote.agent

import app.i.cdms.Constant
import app.i.cdms.api.ApiService
import app.i.cdms.data.model.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

/**
 * @author ZZY
 * 2021/11/6.
 */
class AgentDataSource @Inject constructor(private val service: ApiService) {

    suspend fun withdraw(userId: Int): Result<ApiResult<Any>> {
        return try {
            val response = service.clearAccount(userId = userId)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error withdraw" + e.localizedMessage, e))
        }
    }

    suspend fun transfer(
        toUserId: Int,
        userName: String,
        remark: String?,
        recordType: String,
        changeAmount: Float
    ): Result<ApiResult<Any>> {
        return try {
            val response = service.transfer(
                toUserId = toUserId,
                userName = userName,
                remark = remark,
                recordType = recordType,
                changeAmount = changeAmount
            )
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("Error transfer" + e.localizedMessage, e))
        }
    }

    // 根据用户名配置价格
    suspend fun updateChannelByUsername(
        username: String,
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        limitAddCommission: Float
    ): Result<SCFResult> {
        val payload = JSONObject()
            .put("username", username)
            .put("firstWeight", firstWeight)
            .put("firstCommission", firstCommission)
            .put("addCommission", addCommission)
            .put("limitAddCommission", limitAddCommission)
            .toString()
            .toRequestBody("application/json".toMediaType())

        return try {
            val response = service.updateChannelByUsername(payload = payload)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("updateChannelByUsername fail", e))
        }
    }

    // 根据用户ID配置价格
    suspend fun updateChannelByUserId(
        userId: Int,
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        doConfig: Int
    ): Result<SCFResult> {
        val payload = JSONObject()
            .put("userId", userId)
            .put("firstWeight", firstWeight)
            .put("firstCommission", firstCommission)
            .put("addCommission", addCommission)
            .put("doConfig", doConfig) // 0:Nothing, 1:addUserConfig, 2:updateUserConfig
            .toString()
            .toRequestBody("application/json".toMediaType())

        return try {
            val response = service.updateChannelByUserId(payload = payload)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                // TODO: 2021/10/19
                Result.Error(Exception(response.toString()))
            }
        } catch (e: Throwable) {
            Result.Error(IOException("updateChannelByUserId fail", e))
        }
    }

    suspend fun getAllChannelConfig(channelId: Int): Response<ApiResult<UserChannelConfig>> {
        return service.getAllChannelConfig(Constant.API_GET_CHILDREN_PRICE_BY_CHANNEL + "/$channelId")
    }

    suspend fun getOrderCount(userId: Int): Response<ApiResult<OrderCount>> {
        return service.getOrderCount(userId = userId)
    }
}
