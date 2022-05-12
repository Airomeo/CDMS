package app.i.cdms.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.model.*
import app.i.cdms.repository.channel.ChannelRepository
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.repository.team.TeamRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.ChannelUtil
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val teamRepository: TeamRepository,
    private val channelRepository: ChannelRepository
) : ViewModel() {

    private val _customerChannels = MutableStateFlow<List<ChannelsOf<CustomerChannel>>>(emptyList())
    val customerChannels = _customerChannels.asStateFlow()
    val channelsFlow = _customerChannels.mapLatest { value ->
        val channels = arrayListOf<Channel>()
        value.forEach {
            it.mapNotNull().forEach { customerChannel ->
                channels.addAll(parseToChannels(customerChannel))
            }
        }
        channels
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // 要添加/更新/删除的下级用户、渠道、抽佣相关参数
    var selectedChannelOfUsers = SelectedChannelOfUsers()

    var myTeam: PageOf<Agent>? = null

    init {
        fetchCustomerChannels(null, null)
    }

    // 获取所有渠道详细信息
    private fun fetchCustomerChannels(scCode: String?, rcCode: String?) {
        viewModelScope.launch {
            val list = mutableListOf<ChannelsOf<CustomerChannel>>()
            val customerType = listOf("kd", "ky", "poizon")
            for (customer in customerType) {
                val result = mainRepository.fetchCustomerChannels(customer, scCode, rcCode)
                result?.data?.let { list.add(it) }
            }
            _customerChannels.value = list
        }
    }

    private fun parseToChannels(customerChannel: CustomerChannel): List<Channel> {
        return if (customerChannel.channelPrices.contains("折扣")) {
            ChannelUtil.parseToDiscountZone(customerChannel.channelPrices).map { zone ->
                Channel(
                    "discount",
                    customerChannel.customerName + zone.zone,
                    customerChannel.deliveryType,
                    customerChannel.limitWeight,
                    zone,
                    customerChannel.areaType,
                    customerChannel.backFeeType,
                    customerChannel.lightGoods,
                    customerChannel.customerType,
                )
            }
        } else {
            ChannelUtil.parseToProfitZone(customerChannel.channelPrices).map { zone ->
                Channel(
                    "profit",
                    customerChannel.customerName + zone.zone,
                    customerChannel.deliveryType,
                    customerChannel.limitWeight,
                    zone.blocks,
                    customerChannel.areaType,
                    customerChannel.backFeeType,
                    customerChannel.lightGoods,
                    customerChannel.customerType,
                )
            }
        }
    }

    // 获取我的所有下级
    fun getMyTeam(
        pageNum: Int,
        pageSize: Int,
        userName: String?,
        parentUserId: Int?,
        than: String?,
        balance: String?,
    ) {
        viewModelScope.launch {
            val result =
                teamRepository.getMyTeam(pageNum, pageSize, userName, parentUserId, than, balance)
            myTeam = result
        }
    }

    // 更新/绑定渠道至用户
    fun updateChannel() {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_waiting))
            // 第零步：遍历所有的商家渠道
            val jobs = mutableListOf<Job>()
            for (customerId in selectedChannelOfUsers.customerIds) {
                // 第一步：获取商家渠道的详细信息
                val result = channelRepository.getCustomerChannelPrice(customerId)
                val customerChannelZoneList = result?.data ?: emptyList()
                for (item in customerChannelZoneList) {
                    jobs.add(launch Add@{
                        // 第二步：获取当前未绑定该渠道的用户
                        val result1 = channelRepository.fetchUserPrice(item.channelId, false)
                        // 第三步：处理新的抽佣
                        handleLogic(
                            result1?.data,
                            item.calcFeeType,
                            item.price
                        ) { newPrice, userIds ->
                            // 第四步：绑定配置
                            channelRepository.addUserPrice(
                                item.calcFeeType,
                                item.channelId,
                                newPrice,
                                userIds
                            )
                        }
                    })
                    jobs.add(launch Update@{
                        // 第二步：获取当前已绑定该渠道的用户
                        val result1 = channelRepository.fetchUserPrice(item.channelId, true)
                        // 第三步：处理新的抽佣
                        handleLogic(
                            result1?.data,
                            item.calcFeeType,
                            item.price
                        ) { newPrice, userIds ->
                            // 第四步：更新配置
                            channelRepository.updateUserPrice(
                                item.calcFeeType,
                                item.channelId,
                                newPrice,
                                userIds
                            )
                        }
                    })
                }
            }
            jobs.joinAll()
            EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_finished))
        }
    }

    /**
     * 按照格式处理价格文案，并执行绑定/更新操作
     *
     * @param childChannelPriceList: List<ChildChannelPrice>?
     * @param calcFeeType: String,
     * @param price: String,
     * @param block: suspend (newPrice: String, userIds: List<Int>) -> Unit
     * @return
     */
    private suspend fun handleLogic(
        childChannelPriceList: List<ChildChannelPrice>?,
        calcFeeType: String,
        price: String,
        block: suspend (newPrice: String, userIds: List<Int>) -> Unit
    ) {
        if (childChannelPriceList.isNullOrEmpty()) return
        val userIds = childChannelPriceList.map { it.userId } // 所有用户
        var newPrice = ""
        when (calcFeeType) {
            "profit" -> {
                // "price": "1-1公斤,价格5.1续0;2-3公斤,价格6.9续0;4-50公斤,价格8.6续1.8;",
                val split = price.split("公斤,价格", "续", ";")
                for (i in 0 until split.size / 3) {
                    val area = split[i * 3]
                    val first = String.format(
                        "%.2f",
                        split[i * 3 + 1].toFloat() + selectedChannelOfUsers.firstCommission!!
                    )
                    val add = String.format(
                        "%.2f",
                        split[i * 3 + 2].toFloat() + selectedChannelOfUsers.addCommission!!
                    )
                    newPrice = "$newPrice$area=$first+$add;"
                }
                newPrice = newPrice.dropLast(1) // "1-1=5.1+0;2-3=6.9+0;4-50=8.6+1.8"
            }
            "discount" -> {

            }
        }
        // do my logic
        block(newPrice, userIds)
    }

    fun deleteUserPrice() {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_waiting))
            // 第零步：遍历所有的商家渠道
            val jobs = mutableListOf<Job>()
            for (customerId in selectedChannelOfUsers.customerIds) {
                // 第一步：获取商家渠道的详细信息
                val result = channelRepository.getCustomerChannelPrice(customerId)
                val customerChannelZoneList = result?.data ?: emptyList()
                for (item in customerChannelZoneList) {
                    jobs.add(launch Delete@{
                        // 第二步：获取当前未绑定该渠道的用户
                        val result1 = channelRepository.fetchBindUserPrice(item.channelId, 1, 9999)
                        if (result1?.rows.isNullOrEmpty()) return@Delete
                        val childChannelPriceIds = result1!!.rows!!
                            .filter { it.userId in selectedChannelOfUsers.userIds }
                            .map { it.id!! }
                        channelRepository.deleteUserPrice(childChannelPriceIds)
                    })
                }
            }
            jobs.joinAll()
            EventBus.produceEvent(BaseEvent.Toast(R.string.channel_update_config_finished))
        }
    }
}

data class SelectedChannelOfUsers(
    val firstCommission: Float? = null,
    val addCommission: Float? = null,
    val discountPercent: Float? = null,
    val perAdd: Float? = null,
    val customerIds: List<Int> = emptyList(),
    val userIds: List<Int> = emptyList()
)

enum class ChannelOperation {
    ADD_AND_UPDATE,
    DELETE
}