package app.i.cdms.ui.orderlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.RawOrder
import app.i.cdms.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val repository: OrderRepository) : ViewModel() {

    private var rawOrderList = emptyList<RawOrder>()
    var deliveryType: String = "STO-INT"
    var uiState by mutableStateOf(State())
        private set

    init {
        fetchOrderList()
    }

    private fun fetchOrderList() {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val now = LocalDateTime.now()
            val endTime = now.format(formatter)
            val startTime = now.minusDays(7).format(formatter)

            val result = repository.fetchOrderList(
                deliveryType,
                "kd",
                beginTime = startTime,
                endTime = endTime
            )
            rawOrderList = result?.rows?.map { it.copy(deliveryType = deliveryType) } ?: emptyList()
            uiState = uiState.copy(filteredRawOrderList = filterRawOrder(uiState.keywords))
        }
    }

    fun updateOrderWeightState(deliveryType: String, orderIds: List<Int>) {
        viewModelScope.launch {
            val result = repository.updateOrderWeightState(deliveryType, orderIds)
            if (result?.code == 200) {
                fetchOrderList()
            }
        }
    }

    fun batchCancelOrder(deliveryType: String, deliveryIds: List<String>) {
        viewModelScope.launch {
            val result = repository.batchCancelOrder(deliveryType, deliveryIds)
            fetchOrderList()
            result?.msg?.let {
                showMessage(it)
            }
        }
    }

    fun dialogMessageShown(message: Any) {
        uiState = uiState.copy(messages = uiState.messages.minus(message))
    }

    fun showMessage(message: Any) {
        uiState = uiState.copy(messages = uiState.messages.plus(message))
    }

    fun search(keywords: String) {
//        uiState = uiState.copy(keywords = keywords)
        uiState = uiState.copy(filteredRawOrderList = filterRawOrder(keywords), keywords = keywords)
    }

    fun updateTopAppBarState(topAppBarState: TopAppBarState) {
        uiState = uiState.copy(topAppBarState = topAppBarState)
    }

    fun updateTabIndex(index: Int, deliveryType: String) {
        uiState = uiState.copy(tabIndex = index)
        this.deliveryType = deliveryType
        fetchOrderList()
    }

    private fun filterRawOrder(keywords: String) = rawOrderList.filter {
        it.deliveryId.contains(keywords)
                || it.senderName.contains(keywords)
                || it.receiveName.contains(keywords)
                || it.senderTel?.contains(keywords) == true
                || it.receiveTel?.contains(keywords) == true
                || it.senderMobile?.contains(keywords) == true
                || it.receiveMobile?.contains(keywords) == true
                || it.userName.contains(keywords)
    }

    data class State(
        val isLoading: Boolean = false,
        val messages: List<Any> = emptyList(), // String or Integer resource
        val filteredRawOrderList: List<RawOrder> = emptyList(),
        val tabIndex: Int = 0,
        val topAppBarState: TopAppBarState = TopAppBarState.DEFAULT,
        val keywords: String = ""
    )

    enum class TopAppBarState {
        DEFAULT,
        SEARCH,
    }
}