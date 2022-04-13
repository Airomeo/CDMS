package app.i.cdms.ui.fees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Area
import app.i.cdms.data.model.Channel
import app.i.cdms.data.model.ChannelsOf
import app.i.cdms.data.model.CustomerChannel
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.utils.ChannelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FeesViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    private val _customerChannels = MutableStateFlow<List<ChannelsOf<CustomerChannel>>>(emptyList())
    val channels = _customerChannels.mapLatest { value ->
        arrayListOf<Channel>().apply {
            value.forEach {
                it.mapNotNull().forEach { customerChannel ->
                    addAll(parseToChannels(customerChannel))
                }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var areaList: List<Area>? = null

    init {
        initAreaList()
    }

    /**
     * init AreaList from local datastore or network
     *
     * @return
     */
    private fun initAreaList() {
        viewModelScope.launch {
            mainRepository.areaListFlow.collectLatest {
                if (it == null) {
                    mainRepository.getAndUpdateAreaList()
                } else {
                    areaList = it
                }
            }
        }
    }

    // 获取所有渠道详细信息
    fun fetchCustomerChannels(scCode: String?, rcCode: String?) {
        viewModelScope.launch {
            val list = mutableListOf<ChannelsOf<CustomerChannel>>()
            if (scCode != null && rcCode != null) {
                val customerType = listOf("kd", "ky", "poizon")
                for (customer in customerType) {
                    val result = mainRepository.fetchCustomerChannels(customer, scCode, rcCode)
                    result?.data?.let { list.add(it) }
                }
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
}