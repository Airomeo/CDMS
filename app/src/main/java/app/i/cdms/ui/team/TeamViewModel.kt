package app.i.cdms.ui.team

import androidx.lifecycle.*
import app.i.cdms.data.model.*
import app.i.cdms.repository.team.TeamRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class TeamViewModel(private val teamRepository: TeamRepository) : ViewModel() {

    val _filter = MutableLiveData<AgentFilter>().apply {
        value = AgentFilter()
    }
    private val filter: LiveData<AgentFilter> = _filter

    private val _records = MutableLiveData<ApiResult<MyTeam>>()
    val records: LiveData<ApiResult<MyTeam>> = _records

    val agentsUiModel: Flow<List<Agent>> =
        combine(_records.asFlow(), filter.asFlow()) { r: ApiResult<MyTeam>, f: AgentFilter ->
            r.data?.records?.let { filterByKey(it, f) } ?: listOf()
        }

    fun getMyTeam(token: Token?, pageNum: Int, pageSize: Int) {
        viewModelScope.launch {
            if (token != null) {
                val result = teamRepository.getMyTeam(token, pageNum, pageSize)
                if (result is Result.Success) {
                    _records.value = result.data
                } else {
                    // TODO: 2021/10/20
                    _records.value =
                        ApiResult(code = 999, data = null, msg = "R.string.Result.Error")
                }
            } else {
                _records.value = ApiResult(code = 999, data = null, msg = "Token is null")
                return@launch
            }
        }
    }

    private fun filterByKey(agents: List<Agent>, filter: AgentFilter): List<Agent> {
        var result = agents
        filter.keyName?.let { result = result.filter { it.userName.contains(filter.keyName) } }
        filter.keyId?.let {
            result = result.filter { it.userId.toString().contains(filter.keyId.toString()) }
        }
        return result
    }
}

data class AgentFilter(
    val keyName: String? = null,
    val keyId: Int? = null
)