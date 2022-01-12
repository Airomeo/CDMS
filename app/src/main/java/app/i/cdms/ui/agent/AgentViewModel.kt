package app.i.cdms.ui.agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.OrderCount
import app.i.cdms.data.model.UserChannelConfig
import app.i.cdms.data.model.UserConfig
import app.i.cdms.repository.agent.AgentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AgentViewModel @Inject constructor(private val agentRepository: AgentRepository) :
    ViewModel() {

    private val _uiState = MutableSharedFlow<AgentUiState>()
    val uiState = _uiState.asSharedFlow()
    private val _userConfig = MutableStateFlow<UserConfig?>(null)
    val userConfig = _userConfig.asStateFlow()
    private val _userChannelConfig = MutableStateFlow<UserChannelConfig?>(null)
    val userChannelConfig = _userChannelConfig.asStateFlow()
    private val _orderCount = MutableStateFlow<OrderCount?>(null)
    val orderCount = _orderCount.asStateFlow()

    fun getOrderCount(userId: Int) {
        viewModelScope.launch {
            val result = agentRepository.getOrderCount(userId)
            _orderCount.value = result?.data
        }
    }

    fun withdraw(userId: Int) {
        viewModelScope.launch {
            val result = agentRepository.withdraw(userId)
            result?.let {
                _uiState.emit(AgentUiState.WithdrawSuccess)
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
            val result = agentRepository.transfer(
                toUserId, userName, remark, recordType, changeAmount
            )
            result.let {
                _uiState.emit(AgentUiState.TransferSuccess(changeAmount))
            }
        }
    }

    fun updateChannelByUserId(
        userId: Int,
        firstWeight: Float,
        firstCommission: Float,
        addCommission: Float,
        doConfig: Int
    ) {
        viewModelScope.launch {
            val result = agentRepository.updateChannelByUserId(
                userId,
                firstWeight,
                firstCommission,
                addCommission,
                doConfig
            )
            result?.let {
                _uiState.emit(AgentUiState.UpdateChannelSuccess(it.errorMessage))
            }
        }
    }

    fun getAllChannelConfig(channelId: Int) {
        viewModelScope.launch {
            val result = agentRepository.getAllChannelConfig(channelId)
            _userChannelConfig.value = result?.data
        }
    }
}

// Represents different states for the Agent screen
sealed class AgentUiState {
    data class TransferSuccess(val changeAmount: Float) : AgentUiState()
    data class UpdateChannelSuccess(val msg: String) : AgentUiState()
    object WithdrawSuccess : AgentUiState()
}