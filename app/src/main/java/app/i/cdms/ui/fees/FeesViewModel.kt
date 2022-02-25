package app.i.cdms.ui.fees

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Area
import app.i.cdms.data.model.BookChannelDetail
import app.i.cdms.data.model.CompareFeeBody
import app.i.cdms.repository.book.BookRepository
import app.i.cdms.repository.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeesViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    // 比价请求需要提交的参数
    private val _compareFeeBody = MutableStateFlow(CompareFeeBody())
    val compareFeeBody = _compareFeeBody.asStateFlow()

    // 可用渠道列表
    private val _bookChannelDetailList = MutableStateFlow<List<BookChannelDetail>?>(null)
    val bookChannelDetailList = _bookChannelDetailList.asStateFlow()

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

    fun updateCompareFeeBody(compareFeeBody: CompareFeeBody) {
        _compareFeeBody.value = compareFeeBody
    }

    fun updateBookChannelDetailList(bookChannelDetailList: List<BookChannelDetail>?) {
        _bookChannelDetailList.value = bookChannelDetailList
    }
}