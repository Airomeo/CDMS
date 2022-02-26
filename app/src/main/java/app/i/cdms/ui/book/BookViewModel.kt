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

    // 预下单结果
    // private val _preOrderFeeResult = MutableStateFlow<PreOrderFeeResult?>(null)
    // val preOrderFeeResult = _preOrderFeeResult.asStateFlow()
    private val _preOrderFeeResult = MutableSharedFlow<PreOrderFeeResult?>()
    val preOrderFeeResult = _preOrderFeeResult.asSharedFlow()

    // 订单号
    private val _deliveryId = MutableSharedFlow<String?>()
    val deliveryId = _deliveryId.asSharedFlow()


    // 可用渠道列表
    private val _bookChannelDetailList = MutableStateFlow<List<BookChannelDetail>?>(null)
    val bookChannelDetailList = _bookChannelDetailList.asStateFlow()

    // 下单请求需要提交的参数
    private val _bookBody = MutableStateFlow(BookBody())
    val bookBody = _bookBody.asStateFlow()

    // 比价请求需要提交的参数
    val compareFeeBody = _bookBody.map {
        CompareFeeBody(
            it.customerType,
            it.receiveCity,
            it.receiveValues?.getOrNull(1),
            it.senderCity,
            it.senderValues?.getOrNull(1),
            it.weight,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), CompareFeeBody())

    // 用户当前选择的快递类型
    val selectedChannel = _bookBody.map {
        it.deliveryType to it.customerType
    }

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
     * 获取所有可用渠道信息
     *
     * @return
     */
    fun getCompareFee() {
        viewModelScope.launch {
            val result = mutableListOf<BookChannelDetail>()
            val personalResult =
                bookRepository.getCompareFee(compareFeeBody.value.copy(customerType = "personal"))
            personalResult?.data?.let {
                it.forEach { channel ->
                    channel.customerType = "personal"
                    result.add(channel)
                }
            }
            val businessResult =
                bookRepository.getCompareFee(compareFeeBody.value.copy(customerType = "business"))
            businessResult?.data?.let {
                it.forEach { channel ->
                    channel.customerType = "business"
                    result.add(channel)
                }
            }
            _bookChannelDetailList.value = result
        }
    }

    /**
     * 选中某一个渠道后预下单，返回预计费用等信息
     *
     * @return
     */
    fun getPreOrderFee() {
        viewModelScope.launch {
            val result = bookRepository.getPreOrderFee(bookBody.value)
            _preOrderFeeResult.emit(result?.data)
        }
    }

    /**
     * 下单
     *
     * @return
     */
    fun book() {
        viewModelScope.launch {
            if (bookBody.value.deliveryType == "STO-INT") {
                updateBookBodyFlow(
                    bookBody.value.copy(
                        qty = bookBody.value.packageCount,// qty暂时用packageCount这个替代
                        unitPrice = 2000 //申通无保价费,提供对应货值证明,最高赔付金额2000元,单价请勿超过2000元
                    )
                )
            }
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

    fun updateBookChannelDetailList(bookChannelDetailList: List<BookChannelDetail>?) {
        _bookChannelDetailList.value = bookChannelDetailList
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