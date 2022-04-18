package app.i.cdms.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.*
import app.i.cdms.repository.book.BookRepository
import app.i.cdms.repository.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

//    val _parseAddressResult = MutableStateFlow<ParsedAddressByJd?>(null)
//    val parseAddressResult = _parseAddressResult.asStateFlow()

    // 顺丰地址解析结果
    private val _parsedAddressBySf = MutableStateFlow<List<ParsedAddressBySf>?>(null)
    val parsedAddressBySf = _parsedAddressBySf.asStateFlow()

    // 订单号
    private val _deliveryId = MutableSharedFlow<String?>()
    val deliveryId = _deliveryId.asSharedFlow()

    // 下单请求需要提交的参数
    var bookBody = BookBody()
        private set

    // 可用渠道列表
    var smartPreOrderChannels = emptyList<PreOrderChannel>()

    // 当前选中的渠道
    private val _selectedPreOrderChannel = MutableStateFlow<PreOrderChannel?>(null)
    val selectedPreOrderChannel = _selectedPreOrderChannel.asStateFlow()

    private val _event = MutableSharedFlow<Event>()
    val uiEvent = _event.asSharedFlow()

    sealed class Event {
        data class ShowBookResult(val result: BookResult?) : Event()
        object BookInfoIncomplete : Event()
    }

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

    /**
     * 地址解析
     *
     * @param rawAddress: 元地址文本
     * @return
     */
    fun parseAddress(rawAddress: String) {
        viewModelScope.launch {
//            val result = bookRepository.parseAddressByJd(rawAddress)
//            _parseAddressResult.value = result?.result
            val result = bookRepository.parseAddressBySf(rawAddress)
            _parsedAddressBySf.value = result?.result
        }
    }

    fun clearParsedAddressBySf() {
        _parsedAddressBySf.value = null
    }

    /**
     * 智能下单，返回所有渠道预计费用等信息
     *
     * @return
     */
    private suspend fun fetchSmartPreOrderChannels(body: BookBody): List<PreOrderChannel> {
        return if (body.isReadyForPreOrder) {
            // 按价格升序排序
            bookRepository.fetchSmartPreOrderChannels(body)?.data?.mapNotNull()
                ?.sortedBy { it.preOrderFee.toFloat() } ?: emptyList()
        } else {
            emptyList()
        }
    }

    /**
     * 下单
     *
     * @return
     */
    fun book() {
        viewModelScope.launch {
            if (bookBody.isReadyForOrder) {
                val result = bookRepository.submitOrder(bookBody)
                _event.emit(Event.ShowBookResult(result?.data))
            } else {
                _event.emit(Event.BookInfoIncomplete)
            }
        }
    }

    /**
     * 更改bookBody
     *
     * @param block:
     * @return
     */
    fun bookBodyChanged(block: (bookBody: BookBody) -> BookBody) {
        viewModelScope.launch {
            val oldBody = bookBody
            bookBody = block.invoke(bookBody)

            // 判断是否需要查询价格
            if (oldBody.toPreOrderBody() != bookBody.toPreOrderBody()) {
                smartPreOrderChannels = fetchSmartPreOrderChannels(bookBody.toPreOrderBody())
                bookBody = bookBody.copy(
                    deliveryType = smartPreOrderChannels.getOrNull(0)?.deliveryType,
                    channelId = smartPreOrderChannels.getOrNull(0)?.channelId
                )
            }

            // 更改当前选中的渠道
            _selectedPreOrderChannel.value = smartPreOrderChannels.find {
                it.channelId == bookBody.channelId && it.deliveryType == bookBody.deliveryType
            }
        }
    }

    /**
     * 根据系统订单号查快递单号
     *
     * @param orderNo: 系统订单号
     * @return deliveryId
     */
    fun getDeliveryId(orderNo: String, deliveryType: String) {
        viewModelScope.launch {
            val result = bookRepository.getDeliveryId(orderNo, deliveryType)
            _deliveryId.emit(result?.data)
        }
    }
}