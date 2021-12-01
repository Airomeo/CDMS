package app.i.cdms.ui.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Result
import app.i.cdms.repository.agent.AgentRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AgentViewModel(private val agentRepository: AgentRepository) : ViewModel() {

    private val _uiState = MutableSharedFlow<AgentUiState>()
    val uiState = _uiState.asSharedFlow()

    fun withdraw(userId: Int) {
        viewModelScope.launch {
            _uiState.emit(AgentUiState.Loading)
            val result = agentRepository.withdraw(userId)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        _uiState.emit(AgentUiState.WithdrawSuccess)
                    }
                    else -> {
                        _uiState.emit(AgentUiState.Error(Exception(result.data.msg)))
                    }
                }
            } else if (result is Result.Error) {
                _uiState.emit(AgentUiState.Error(result.exception))
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
            _uiState.emit(AgentUiState.Loading)
            val result = agentRepository.transfer(
                toUserId, userName, remark, recordType, changeAmount
            )
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        _uiState.emit(AgentUiState.TransferSuccess(changeAmount))

                    }
                    else -> {
                        _uiState.emit(AgentUiState.Error(Exception(result.data.msg)))
                    }
                }
            } else if (result is Result.Error) {
                _uiState.emit(AgentUiState.Error(result.exception))
            }
        }
    }
}

// Represents different states for the Agent screen
sealed class AgentUiState {
    data class Error(val exception: Throwable) : AgentUiState()
    data class TransferSuccess(val changeAmount: Float) : AgentUiState()
    object WithdrawSuccess : AgentUiState()
    object Loading : AgentUiState()
    object None : AgentUiState()
}