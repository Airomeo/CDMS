package app.i.cdms.data.source.remote.agent

import app.i.cdms.api.ApiService
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.SCFResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

/**
 * @author ZZY
 * 2021/11/6.
 */
class AgentDataSource(private val service: ApiService) {

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
        firstCommission: Float,
        additionalCommission: Float
    ): Result<SCFResult> {
        val payload = JSONObject()
            .put("username", username)
            .put("firstCommission", firstCommission)
            .put("additionalCommission", additionalCommission)
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
        firstCommission: Float,
        additionalCommission: Float
    ): Result<SCFResult> {
        val payload = JSONObject()
            .put("userId", userId)
            .put("firstCommission", firstCommission)
            .put("additionalCommission", additionalCommission)
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
}
