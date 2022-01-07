package app.i.cdms.ui.channel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Channel
import app.i.cdms.data.model.Result
import app.i.cdms.repository.agent.AgentRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ChannelViewModel @Inject constructor(private val agentRepository: AgentRepository) :
    ViewModel() {
    // TODO: Implement the ViewModel

    private val _uiState = MutableSharedFlow<ChannelUiState>()
    val uiState = _uiState.asSharedFlow()

    fun getUserPrice(userId: Int?) {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = agentRepository.getUserPrice(userId)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            _uiState.emit(ChannelUiState.GetPriceSuccess(it))
                        }
                        EventBus.produceEvent(BaseEvent.Nothing)
                    }
                    401 -> {
                        EventBus.produceEvent(BaseEvent.NeedLogin)
                    }
                    else -> {
                        EventBus.produceEvent(BaseEvent.Failed(result.data))
                    }
                }
            } else if (result is Result.Error) {
                EventBus.produceEvent(BaseEvent.Error(result.exception))
            }
        }
    }
}

// Represents different states for the Agent screen
sealed class ChannelUiState {
    data class GetPriceSuccess(val list: List<Channel>) : ChannelUiState()
}