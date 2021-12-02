package app.i.cdms.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Agent
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.repository.team.TeamRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TeamViewModel(private val teamRepository: TeamRepository) : ViewModel() {

    private val _filter = MutableStateFlow(AgentFilter())
    val filter = _filter.asStateFlow()

    // TODO: 2021/11/15 用MutableSharedFlow还是MutableStateFlow？如果是MutableStateFlow，如何初始化？这段代码还有没有优化空间？
    private val _myTeam =
        MutableStateFlow(MyTeam(1, 1, 1, true, listOf(), 1, listOf(), true, 1, 1))
    val myTeam = _myTeam.asStateFlow()

//    private lateinit var _mt: MutableStateFlow<MyTeam>
//    val mt = _mt.asStateFlow()

    private val _uiState = MutableSharedFlow<TeamUiState>()
    val uiState = _uiState.asSharedFlow()

    init {
        getMyTeam(1, 9999)
    }

    private fun getMyTeam(pageNum: Int, pageSize: Int) {
        viewModelScope.launch {
            delay(1)
            _uiState.emit(TeamUiState.Loading)
            val result = teamRepository.getMyTeam(pageNum, pageSize)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            _myTeam.emit(it)
                            _uiState.emit(
                                TeamUiState.LoadMyTeam(
                                    filterByKey(
                                        myTeam.value,
                                        filter.value
                                    )
                                )
                            )
                        }
                    }
                    else -> {
                        _uiState.emit(TeamUiState.Error(Exception(result.data.msg)))
                    }
                }
            } else if (result is Result.Error) {
                _uiState.emit(TeamUiState.Error(result.exception))
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
        viewModelScope.launch {
            _filter.value = agentFilter
            _uiState.emit(TeamUiState.LoadSearchResult(filterByKey(myTeam.value, filter.value)))
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
sealed class TeamUiState {
    //    object Success : UiState()
    data class Error(val exception: Throwable) : TeamUiState()
    data class LoadMyTeam(val myTeam: MyTeam) : TeamUiState()
    data class LoadSearchResult(val myTeam: MyTeam) : TeamUiState()

    //    data class Loading(val msg: String) : TeamUiState()
    object Loading : TeamUiState()
    object None : TeamUiState()
}