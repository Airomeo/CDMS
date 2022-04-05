package app.i.cdms.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Channel
import app.i.cdms.data.model.CustomerChannel
import app.i.cdms.data.model.CustomerChannels
import app.i.cdms.data.model.MyTeam
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.utils.ChannelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject


@HiltViewModel
class ChannelViewModel @Inject constructor(private val mainRepository: MainRepository) :
    ViewModel() {

    private val _customerChannels = MutableStateFlow<List<CustomerChannels>>(emptyList())
    val channelsFlow = _customerChannels.mapLatest { value ->
        val channels = Collections.synchronizedList(arrayListOf<Channel>())
        value.forEach {
            it.mapNotNull().forEach { list ->
                list.forEach { customerChannel ->
                    channels.addAll(parseToChannels(customerChannel))
                }
            }
        }
        channels
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _myTeam = MutableStateFlow<MyTeam?>(null)
    val myTeam = _myTeam.asStateFlow()

    init {
        fetchCustomerChannels()
    }

    // 获取所有渠道详细信息
    private fun fetchCustomerChannels() {
        viewModelScope.launch {
            val list = mutableListOf<CustomerChannels>()
            val customerType = listOf("personal", "business", "poizon")
            for (customer in customerType) {
                val result = mainRepository.fetchCustomerChannels(customer)
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
                    customerChannel.priority,
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
                    customerChannel.priority,
                    customerChannel.backFeeType,
                    customerChannel.lightGoods,
                    customerChannel.customerType,
                )
            }
        }
    }

    // 获取渠道详细信息
    private fun getChannelDetail(customerChannel: CustomerChannel) {
        viewModelScope.launch {
//            val result = mainRepository.getCustomerChannelDetail(customerChannel.id)
//            result?.data?.let {
//                for (item in it) {
//                    val copy = item.copy(customerChannel = customerChannel)
//                    channelDetailList.add(copy)
//                }
//            }
        }
    }
}