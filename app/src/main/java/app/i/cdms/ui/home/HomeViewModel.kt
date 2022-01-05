package app.i.cdms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.NoticeList
import app.i.cdms.data.model.Result
import app.i.cdms.repository.home.HomeRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _myInfo = MutableStateFlow<MyInfo?>(null)
    val myInfo = _myInfo.asSharedFlow()
    private val _noticeList = MutableStateFlow<NoticeList?>(null)
    val noticeList = _noticeList.asStateFlow()

    init {
        getMyInfo()
    }

    private fun getNotice() {
        viewModelScope.launch {
            EventBus.produceEvent(BaseEvent.Loading)
            val result = homeRepository.getNotice()
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        EventBus.produceEvent(BaseEvent.None)
                        _noticeList.emit(result.data)
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

    private fun getMyInfo() {
        viewModelScope.launch {
            delay(1)
            // can be launched in a separate asynchronous job
            EventBus.produceEvent(BaseEvent.Loading)
            val result = homeRepository.getMyInfo()
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            EventBus.produceEvent(BaseEvent.None)
                            _myInfo.value = it
                            updateMyInfo(it)
                            getNotice()
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

    private fun updateMyInfo(myInfo: MyInfo) {
        viewModelScope.launch {
            homeRepository.updateMyInfo(myInfo)
        }
    }
}