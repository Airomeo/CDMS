package app.i.cdms.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * @author ZZY
 * 2021/11/30.
 */
object EventBus : ViewModel() {

    private val _events = MutableSharedFlow<BaseEvent>() // private mutable shared flow
    val events = _events.asSharedFlow() // publicly exposed as read-only shared flow

    fun produceEvent(event: BaseEvent) {
        viewModelScope.launch {
            _events.emit(event) // suspends until all subscribers receive it
        }
    }
}

sealed class BaseEvent {
    data class Error(val exception: Throwable) : BaseEvent() // 方法或请求执行出错/异常/失败，或有未处理的情况
    data class Failed(val data: Any?) : BaseEvent() // 请求成功，空结果（可能是网络异常）或者其他结果
    data class Toast(val resId: Int) : BaseEvent()
    object Loading : BaseEvent()
    object Nothing : BaseEvent()
    object NeedLogin : BaseEvent()
    object Refresh : BaseEvent()
}