package app.i.cdms.ui.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class AgentViewModel @Inject constructor(private val agentRepository: AgentRepository) :
    ViewModel() {

    private val _uiState = MutableSharedFlow<AgentUiState>()
    val uiState = _uiState.asSharedFlow()

    fun withdraw(userId: Int) {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = agentRepository.withdraw(userId)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        _uiState.emit(AgentUiState.WithdrawSuccess)
                        EventBus.produceEvent(BaseEvent.None)
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

    fun transfer(
        toUserId: Int,
        userName: String,
        remark: String?,
        recordType: String,
        changeAmount: Float
    ) {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = agentRepository.transfer(
                toUserId, userName, remark, recordType, changeAmount
            )
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        EventBus.produceEvent(BaseEvent.None)
                        _uiState.emit(AgentUiState.TransferSuccess(changeAmount))
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

    fun updateChannelByUserId(
        userId: Int,
        firstCommission: Float,
        additionalCommission: Float
    ) {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = agentRepository.updateChannelByUserId(
                userId,
                firstCommission,
                additionalCommission
            )
            if (result is Result.Success) {
                when (result.data.errorCode) {
                    200 -> {
                        EventBus.produceEvent(BaseEvent.None)
                        _uiState.emit(AgentUiState.UpdateChannelSuccess(result.data.errorMessage))
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
sealed class AgentUiState {
    data class TransferSuccess(val changeAmount: Float) : AgentUiState()
    data class UpdateChannelSuccess(val msg: String) : AgentUiState()
    object WithdrawSuccess : AgentUiState()
}