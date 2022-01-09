package app.i.cdms.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.ChannelDetail
import app.i.cdms.data.model.CustomerChannel
import app.i.cdms.repository.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChannelViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    private val channelDetailList = mutableListOf<ChannelDetail>()
    private val _channelDetailListFlow = MutableStateFlow<List<ChannelDetail>>(listOf())
    val channelDetailListFlow = _channelDetailListFlow.asStateFlow()

    fun getAllChannelDetail() {
        viewModelScope.launch {
            val customerType = listOf("personal", "business", "poizon")
            for (customer in customerType) {
                val result = mainRepository.getCustomerChannel(customer)
                result?.data ?: return@launch
                val customerChannelResult = result.data
                customerChannelResult.sto?.forEach {
                    getChannelDetail(it)
                }
                customerChannelResult.yto?.forEach {
                    getChannelDetail(it)
                }
                customerChannelResult.jd?.forEach {
                    getChannelDetail(it)
                }
                customerChannelResult.dop?.forEach {
                    getChannelDetail(it)
                }
            }
            _channelDetailListFlow.value = channelDetailList
        }
    }

    private suspend fun getChannelDetail(customerChannel: CustomerChannel) {
        val result = mainRepository.getCustomerChannelDetail(customerChannel.id)
        result?.data?.let {
            for (item in it) {
                item.customerChannel = customerChannel
                channelDetailList.add(item)
            }
        }
    }
}