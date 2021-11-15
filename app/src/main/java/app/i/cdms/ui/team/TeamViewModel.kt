package app.i.cdms.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Agent
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.repository.team.TeamRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamViewModel(private val teamRepository: TeamRepository) : ViewModel() {

    private val _filter = MutableStateFlow(AgentFilter())
    val filter = _filter.asStateFlow()

    // TODO: 2021/11/15 用MutableSharedFlow还是MutableStateFlow？如果是MutableStateFlow，如何初始化？这段代码还有没有优化空间？
    private val _myTeam =
        MutableStateFlow(MyTeam(1, 1, 1, true, listOf(), 1, listOf(), true, 1, 1))
    val myTeam = _myTeam.asStateFlow()

    // Backing property to avoid state updates from other classes
    private val _uiState = MutableStateFlow<UiState>(UiState.None)

    // The UI collects from this StateFlow to get its state updates
    val uiState = _uiState.asStateFlow()

    init {
        getMyTeam(1, 9999)
    }

    private fun getMyTeam(pageNum: Int, pageSize: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = teamRepository.getMyTeam(pageNum, pageSize)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            _myTeam.emit(it)
                            _uiState.value =
                                UiState.Success(filterByKey(myTeam.value, filter.value))
                        }
                    }
                    else -> {
                        _uiState.value = UiState.Error(Exception(result.data.msg))
                    }
                }
            } else if (result is Result.Error) {
                _uiState.value = UiState.Error(result.exception)
            }
        }
    }

    private fun filterByKey(myTeam: MyTeam, filter: AgentFilter): MyTeam {
        var result = myTeam.records
        filter.keyName?.let { result = result.filter { it.userName.contains(filter.keyName) } }
        filter.keyId?.let {
            result = result.filter { it.userId.toString().contains(filter.keyId.toString()) }
        }
        return myTeam.copy(records = result)
    }

    fun search(agentFilter: AgentFilter) {
        _filter.value = agentFilter
        if (uiState.value is UiState.Success) {
            _uiState.value = UiState.Success(filterByKey(myTeam.value, filter.value))
        }
    }

    fun updateAgentBalanceUIData(newBalance: Float, agent: Agent) {
        val index = myTeam.value.records.indexOf(agent)
        val newAgent = agent.copy(accountBalance = newBalance)
        val newRecords = myTeam.value.records.toMutableList().apply {
            this[index] = newAgent
        }
        _myTeam.value = myTeam.value.copy(records = newRecords)
    }
}

data class AgentFilter(
    val keyName: String? = null,
    val keyId: Int? = null
)

// Represents different states for the Team screen
sealed class UiState {
    //    object Success : UiState()
    data class Error(val exception: Throwable) : UiState()
    data class Success(val myTeam: MyTeam) : UiState()

    //    data class Loading(val msg: String) : UiState()
    object Loading : UiState()
    object None : UiState()
}