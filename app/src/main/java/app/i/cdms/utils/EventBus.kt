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
    data class Error(val exception: Throwable) : BaseEvent()
    data class Failed(val data: Any) : BaseEvent()
    object Loading : BaseEvent()
    object None : BaseEvent()
    object NeedLogin : BaseEvent()
    object Refresh : BaseEvent()
}