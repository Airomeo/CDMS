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
    private val _bookBody = MutableStateFlow(BookBody())
    val bookBody = _bookBody.asStateFlow()

    // 可用渠道列表
    val smartPreOrderChannels = _bookBody
        .mapLatest {
            // 转换成下单前查询可用渠道的参数
            BookBody(
                goods = it.goods,
                packageCount = it.packageCount,
                receiveAddress = it.receiveAddress,
                receiveCity = it.receiveCity,
                receiveDistrict = it.receiveDistrict,
                receiveMobile = it.receiveMobile,
                receiveName = it.receiveName,
                receiveProvince = it.receiveProvince,
                receiveProvinceCode = it.receiveProvinceCode,
                receiveTel = it.receiveTel,
                receiveValues = it.receiveValues,
                senderAddress = it.senderAddress,
                senderCity = it.senderCity,
                senderDistrict = it.senderDistrict,
                senderMobile = it.senderMobile,
                senderName = it.senderName,
                senderProvince = it.senderProvince,
                senderProvinceCode = it.senderProvinceCode,
                senderTel = it.senderTel,
                senderValues = it.senderValues,
                weight = it.weight,
            )
        }
        .distinctUntilChanged()
        .mapLatest { fetchSmartPreOrderChannels(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    // 下单结果
    private val _bookResult = MutableSharedFlow<BookResult?>()
    val bookResultFlow = _bookResult.asSharedFlow()
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
            updateParseAddressBySf(result?.result)
        }
    }

    /**
     * 智能下单，返回所有渠道预计费用等信息
     *
     * @return
     */
    private suspend fun fetchSmartPreOrderChannels(body: BookBody): ChannelsOf<PreOrderChannel>? {
        return if (body.isReadyForPreOrder) {
            bookRepository.fetchSmartPreOrderChannels(body)?.data
        } else {
            null
        }
    }

    /**
     * 下单
     *
     * @return
     */
    fun book() {
        viewModelScope.launch {
            val result = bookRepository.submitOrder(bookBody.value)
            _bookResult.emit(result?.data)
        }
    }

    fun updateParseAddressBySf(parsedAddressBySf: List<ParsedAddressBySf>?) {
        _parsedAddressBySf.value = parsedAddressBySf
    }

    fun updateBookBodyFlow(bookBody: BookBody) {
        _bookBody.value = bookBody
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