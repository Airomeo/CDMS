package app.i.cdms.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Agent
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.repository.team.TeamRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TeamViewModel(private val teamRepository: TeamRepository) : ViewModel() {

    private val _filter = MutableStateFlow(AgentFilter())
    val filter = _filter.asStateFlow()

    private val _myTeam = MutableStateFlow<MyTeam?>(null)
    val myTeam = _myTeam.asStateFlow()

    val uiState = combine(filter, myTeam) { f: AgentFilter, t: MyTeam? ->
        return@combine filterByKey(t, f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        getMyTeam(1, 9999)
    }

    private fun getMyTeam(pageNum: Int, pageSize: Int) {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = teamRepository.getMyTeam(pageNum, pageSize)
            EventBus.produceEvent(BaseEvent.None)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            _myTeam.emit(it)
                        }
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

    private fun filterByKey(myTeam: MyTeam?, filter: AgentFilter): List<Agent> {
        var result = myTeam?.records ?: return emptyList()
        filter.keyName?.let { result = result.filter { it.userName.contains(filter.keyName) } }
        filter.keyId?.let {
            result = result.filter { it.userId.toString().contains(filter.keyId.toString()) }
        }
        return result
    }

    fun search(agentFilter: AgentFilter) {
        _filter.value = agentFilter
    }

    fun updateAgentBalanceUIData(newBalance: Float, agent: Agent) {
        myTeam.value ?: return
        val index = myTeam.value!!.records.indexOf(agent)
        val newRecords = myTeam.value!!.records.toMutableList()
        newRecords[index] = agent.copy(accountBalance = newBalance)

        _myTeam.value = myTeam.value!!.copy(records = newRecords)
    }
}

data class AgentFilter(
    val keyName: String? = null,
    val keyId: Int? = null
)