package app.i.cdms.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Agent
import app.i.cdms.data.model.MyTeam
import app.i.cdms.repository.team.TeamRepository
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
            val result = teamRepository.getMyTeam(pageNum, pageSize, userName)
            _myTeam.value = result?.data
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

    fun updateAgentInMyTeam(oldAgent: Agent) {
        myTeam.value ?: return
        val records = myTeam.value!!.records.toMutableList()
        val index = records.indexOf(oldAgent)
        viewModelScope.launch {
            val result = teamRepository.getMyTeam(index + 1, 1, null)
            result?.data?.let {
                records[index] = it.records.first()
                _myTeam.emit(myTeam.value!!.copy(records = records))
            }
        }
    }
}

data class AgentFilter(
    val keyName: String? = null,
    val keyId: Int? = null
)