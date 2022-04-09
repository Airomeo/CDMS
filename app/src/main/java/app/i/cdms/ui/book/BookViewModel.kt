package app.i.cdms.ui.book

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.*
import app.i.cdms.repository.book.BookRepository
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.utils.ChannelUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
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
    // private val _preOrderFeeResult = MutableStateFlow<ChannelFees?>(null)
    // val preOrderFeeResult = _preOrderFeeResult.asStateFlow()
    private val _preOrderFeeResult = MutableSharedFlow<ChannelFees?>()
    val preOrderFeeResult = _preOrderFeeResult.asSharedFlow()

    // 订单号
    private val _deliveryId = MutableSharedFlow<String?>()
    val deliveryId = _deliveryId.asSharedFlow()

    // 可用渠道列表
    val _channelFees = MutableStateFlow<List<ChannelFees>>(emptyList())
    val channelFees = _channelFees.asStateFlow()
    val channelsFlow = _channelFees.mapLatest { value ->
        value.map {
            Channel(
                it.calcFeeType,
                "¥" + it.preOrderFee + " " + it.channelName,
                it.deliveryType,
                it.limitWeight,
                if (it.calcFeeType == "profit") {
                    ChannelUtil.parsePrice(it.price)
                } else {
                    ChannelUtil.parseToDiscountZone(it.price)[0]
                },
                null,
                null,
                it.lightGoods,
                it.customerType
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    // 下单请求需要提交的参数
    val _bookBody = MutableStateFlow(BookBody())
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
    fun fetchCompareFee() {
        val list = Collections.synchronizedList(arrayListOf<ChannelFees>())
        viewModelScope.launch(Dispatchers.IO) {
            awaitAll(
                async {
                    val result =
                        bookRepository.fetchCompareFee(compareFeeBody.value.copy(customerType = "kd"))
                    result?.data?.let {
                        it.forEach { channel ->
                            channel.customerType = "kd"
                            list.add(channel)
                        }
                    }
                },
                async {
                    val result =
                        bookRepository.fetchCompareFee(compareFeeBody.value.copy(customerType = "ky"))
                    result?.data?.let {
                        it.forEach { channel ->
                            channel.customerType = "ky"
                            list.add(channel)
                        }
                    }
                }
            )
            _channelFees.value = list
        }
    }

    /**
     * 选中某一个渠道后预下单，返回预计费用等信息
     *
     * @return
     */
    fun fetchPreOrderFee() {
        viewModelScope.launch {
            val result = bookRepository.fetchPreOrderFee(bookBody.value)
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
                // qty暂时用packageCount这个替代
                updateBookBodyFlow(bookBody.value.copy(qty = bookBody.value.packageCount))
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