package app.i.cdms.repository.channel

import app.i.cdms.data.source.remote.channel.ChannelDataSource
import app.i.cdms.repository.BaseRepository
import javax.inject.Inject

class ChannelRepository @Inject constructor(private val dataSource: ChannelDataSource) :
    BaseRepository() {

    suspend fun getCustomerChannelPrice(customerId: Int, area: Boolean = false) =
        executeResponse { dataSource.getCustomerChannelPrice(customerId, area) }

    /**
     * 获取下级用户的价格
     *
     * @param channelId: Int
     * @param pageNum: Int
     * @param pageSize: Int
     * @return
     */
    suspend fun fetchBindUserPrice(channelId: Int, pageNum: Int, pageSize: Int) =
        executeResponse { dataSource.fetchBindUserPrice(channelId, pageNum, pageSize) }

    suspend fun fetchUserPrice(channelId: Int, isBind: Boolean) =
        executeResponse { dataSource.fetchUserPrice(channelId, isBind) }

    suspend fun addUserPrice(
        calcFeeType: String,
        channelId: Int,
        price: String,
        userIds: List<Int>
    ) = executeResponse { dataSource.addUserPrice(calcFeeType, channelId, price, userIds) }

    suspend fun updateUserPrice(
        calcFeeType: String,
        channelId: Int,
        price: String,
        userIds: List<Int>
    ) = executeResponse { dataSource.updateUserPrice(calcFeeType, channelId, price, userIds) }

    suspend fun deleteUserPrice(childChannelPriceIds: List<Int>) =
        executeResponse { dataSource.deleteUserPrice(childChannelPriceIds) }
}