package app.i.cdms.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Agent
import app.i.cdms.data.model.MyTeam
import app.i.cdms.data.model.Result
import app.i.cdms.repository.team.TeamRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamViewModel @Inject constructor(private val teamRepository: TeamRepository) : ViewModel() {

    private val _filter = MutableStateFlow(AgentFilter())
    val filter = _filter.asStateFlow()

    private val _myTeam = MutableStateFlow<MyTeam?>(null)
    val myTeam = _myTeam.asStateFlow()

    val uiState = combine(filter, myTeam) { f: AgentFilter, t: MyTeam? ->
        return@combine filterByKey(t, f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        getMyTeam(1, 9999, null)
    }

    private fun getMyTeam(pageNum: Int, pageSize: Int, userName: String?) {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = teamRepository.getMyTeam(pageNum, pageSize, userName)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            EventBus.produceEvent(BaseEvent.None)
                            _myTeam.emit(it)
                        }
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

    fun batchUpdateChannel() {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = teamRepository.batchUpdateChannel()
            if (result is Result.Success) {
                when (result.data.errorCode) {
                    200 -> {
                        EventBus.produceEvent(BaseEvent.Toast(result.data.errorMessage))
                    }
                    401 -> {
                        EventBus.produceEvent(BaseEvent.NeedLogin)
                    }
                    else -> {
                        EventBus.produceEvent(BaseEvent.Failed(result.data))
                    }
                }
            } else if (result is Result.Error) {
                if ("504" in result.exception.toString()) {
                    EventBus.produceEvent(BaseEvent.Toast("执行成功，请于2分钟后检查结果。"))
                } else {
                    EventBus.produceEvent(BaseEvent.Error(result.exception))
                }
            }
        }
    }

    fun updateAgentInMyTeam(oldAgent: Agent) {
        myTeam.value ?: return
        val records = myTeam.value!!.records.toMutableList()
        val index = records.indexOf(oldAgent)
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = teamRepository.getMyTeam(index + 1, 1, null)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            EventBus.produceEvent(BaseEvent.None)
                            records[index] = it.records.first()
                            _myTeam.emit(myTeam.value!!.copy(records = records))
                        }
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

data class AgentFilter(
    val keyName: String? = null,
    val keyId: Int? = null
)