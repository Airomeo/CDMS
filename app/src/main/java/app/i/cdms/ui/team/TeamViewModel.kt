package app.i.cdms.ui.team

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Agent
import app.i.cdms.data.model.AgentLevel
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

    private val _agentLevelList = MutableSharedFlow<List<AgentLevel>>()
    val agentLevelList = _agentLevelList.asSharedFlow()

    private val _inviteCode = MutableSharedFlow<String?>()
    val inviteCode = _inviteCode.asSharedFlow()

    val uiState = combine(filter, myTeam) { f: AgentFilter, t: MyTeam? ->
        return@combine filterByKey(t, f)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        getMyTeam(1, 9999, null, null, null, null)
    }

    private fun getMyTeam(
        pageNum: Int,
        pageSize: Int,
        userName: String?,
        parentUserId: Int?,
        than: String?,
        balance: String?,
    ) {
        viewModelScope.launch {
            val result =
                teamRepository.getMyTeam(pageNum, pageSize, userName, parentUserId, than, balance)
            _myTeam.value = result
        }
    }

    private fun filterByKey(myTeam: MyTeam?, filter: AgentFilter): List<Agent> {
        var result = myTeam?.rows ?: return emptyList()
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
        myTeam.value?.rows ?: return
        val records = myTeam.value!!.rows!!.toMutableList()
        val index = records.indexOf(oldAgent)
        viewModelScope.launch {
            val result = teamRepository.getMyTeam(index + 1, 1, null, null, null, null)
            result?.rows?.let {
                records[index] = it.first()
                _myTeam.emit(myTeam.value!!.copy(rows = records))
            }
        }
    }

    /**
     * 获取当前用户的下级代理有哪些等级
     *
     * @param
     * @return
     */
    fun fetchAgentLevel() {
        viewModelScope.launch {
            val result = teamRepository.fetchAgentLevel()
            _agentLevelList.emit(result?.data ?: emptyList())
        }
    }

    /**
     * 根据所选等级，获取邀请码
     *
     * @param level: 30, 20, 10
     * @return
     */
    fun fetchInviteCode(level: String) {
        viewModelScope.launch { _inviteCode.emit(teamRepository.fetchInviteCode(level)?.data) }
    }
}

data class AgentFilter(
    val keyName: String? = null,
    val keyId: Int? = null
)