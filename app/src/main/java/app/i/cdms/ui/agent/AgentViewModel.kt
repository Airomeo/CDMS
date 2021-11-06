package app.i.cdms.ui.agent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import app.i.cdms.repository.agent.AgentRepository
import kotlinx.coroutines.launch

class AgentViewModel(private val agentRepository: AgentRepository) : ViewModel() {

    private val _apiResult = MutableLiveData<ApiResult<Any>>()
    val apiResult: LiveData<ApiResult<Any>> = _apiResult

    fun withdrawal(token: Token?, userId: Int) {
        viewModelScope.launch {
            if (token != null) {
                val result = agentRepository.withdrawal(token, userId)
                if (result is Result.Success) {
                    _apiResult.value = result.data
                } else {
                    // TODO: 2021/10/20
                    _apiResult.value =
                        ApiResult(code = 999, data = null, msg = "R.string.Result.Error")
                }
            } else {
                _apiResult.value = ApiResult(code = 999, data = null, msg = "Token is null")
                return@launch
            }
        }
    }
}