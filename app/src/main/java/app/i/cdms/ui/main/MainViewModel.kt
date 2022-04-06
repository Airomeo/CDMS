package app.i.cdms.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Router
import app.i.cdms.data.model.Token
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
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

    init {
        initToken()
    }

    private fun initToken() {
        viewModelScope.launch {
            mainRepository.token.collectLatest {
                if (it == null) {
                    EventBus.produceEvent(BaseEvent.NeedLogin)
                } else {
                    fetchRouters()
                }
                Log.d("TAG", "MainViewModel init: token = " + it?.token)
            }
        }
    }

    fun verifyToken() {
        viewModelScope.launch {
        }
    }

    fun updateToken(token: Token?) {
        viewModelScope.launch {
            mainRepository.updateToken(token)
            EventBus.produceEvent(BaseEvent.Refresh)
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
}