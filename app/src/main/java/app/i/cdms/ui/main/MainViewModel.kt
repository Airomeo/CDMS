package app.i.cdms.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.Token
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.utils.Event
import app.i.cdms.utils.EventBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository
) : ViewModel() {

    val token = mainRepository.token.shareIn(viewModelScope, SharingStarted.WhileSubscribed())

    init {
        viewModelScope.launch {
            token.collectLatest {
                if (it.token.isBlank()) {
//                    _uiState.emit(MainUiState.NeedLogin)
                    EventBus.produceEvent(Event.NeedLogin)
                    Log.d("TAG", "MainViewModel init: token.isBlank()")
                } else {
                    EventBus.produceEvent(Event.Refresh)
                    Log.d("TAG", "MainViewModel init: Event.Refresh")
                }
            }
        }
    }

    fun verifyToken() {
        viewModelScope.launch {
        }
    }

    fun updateToken(token: Token) {
        viewModelScope.launch {
            mainRepository.updateToken(token)
        }
    }
}