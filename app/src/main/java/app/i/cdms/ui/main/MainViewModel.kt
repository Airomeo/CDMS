package app.i.cdms.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.BuildConfig
import app.i.cdms.data.model.Token
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
) : ViewModel() {

    val token = mainRepository.token.shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    init {
        viewModelScope.launch {
            token.collectLatest {
                if (it.token.isBlank()) {
                    EventBus.produceEvent(BaseEvent.NeedLogin)
                    Log.d("TAG", "MainViewModel init: token.isBlank()")
                }
            }
        }
        checkUpdate()
    }

    fun verifyToken() {
        viewModelScope.launch {
        }
    }

    fun updateToken(token: Token) {
        viewModelScope.launch {
            mainRepository.updateToken(token)
            EventBus.produceEvent(BaseEvent.Refresh)
        }
    }

    private fun checkUpdate() {
        if (!BuildConfig.DEBUG) {
            viewModelScope.launch {
                val result = mainRepository.checkUpdate()
                result ?: return@launch

                if (result.versionCode > BuildConfig.VERSION_CODE) {
                    EventBus.produceEvent(BaseEvent.Update(result))
                }
            }
        }
    }
}