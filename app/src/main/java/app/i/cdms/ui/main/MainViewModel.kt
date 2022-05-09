package app.i.cdms.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.AgentLevel
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Router
import app.i.cdms.data.model.Token
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {
    val myInfo: MyInfo? = runBlocking { mainRepository.myInfo.firstOrNull() }

    private val _routers = MutableStateFlow<List<Router>>(emptyList())
    val routers = _routers.asStateFlow()

    private val _agentLevelList = MutableSharedFlow<List<AgentLevel>>()
    val agentLevelList = _agentLevelList.asSharedFlow()

    private val _inviteCode = MutableSharedFlow<String?>()
    val inviteCode = _inviteCode.asSharedFlow()
    val token = mainRepository.token.distinctUntilChanged()

    init {
        verifyToken()
    }

    private fun verifyToken() {
        viewModelScope.launch {
            token.collectLatest {
                if (it == null) {
                    EventBus.produceEvent(BaseEvent.Auth)
                } else {
                    fetchRouters()
                    EventBus.produceEvent(BaseEvent.Refresh)
                }
                Log.e("TAG", "MainViewModel token = " + it?.token)
            }
        }
    }

    fun updateToken(token: Token?) {
        viewModelScope.launch {
            mainRepository.updateToken(token)
        }
    }

    /**
     * 获取全局路由，以便管理功能入口
     *
     * @param :
     * @return
     */
    private fun fetchRouters() {
        viewModelScope.launch {
            val result = mainRepository.fetchRouters()
            _routers.value = result?.data ?: emptyList()
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
            val result = mainRepository.fetchAgentLevel()
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
        viewModelScope.launch { _inviteCode.emit(mainRepository.fetchInviteCode(level)?.data) }
    }
}