package app.i.cdms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Result
import app.i.cdms.repository.home.HomeRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _myInfo = MutableStateFlow<MyInfo?>(null)
    val myInfo = _myInfo.asSharedFlow()

    init {
        getMyInfo()
    }

    private fun getMyInfo() {
        viewModelScope.launch {
            delay(1)
            // can be launched in a separate asynchronous job
            EventBus.produceEvent(BaseEvent.Loading)
            val result = homeRepository.getMyInfo()
            EventBus.produceEvent(BaseEvent.None)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            _myInfo.value = it
                            updateMyInfo(it)
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

    private fun updateMyInfo(myInfo: MyInfo) {
        viewModelScope.launch {
            homeRepository.updateMyInfo(myInfo)
        }
    }
}