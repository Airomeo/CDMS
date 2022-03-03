package app.i.cdms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.NoticeList
import app.i.cdms.repository.home.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun getNotice() {
        viewModelScope.launch {
            _noticeList.value = homeRepository.getNotice()
        }
    }

    private fun getMyInfo() {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            val result = homeRepository.getMyInfo()
            _myInfo.value = result?.data
        }
    }

    fun updateMyInfo(myInfo: MyInfo?) {
        viewModelScope.launch {
            homeRepository.updateMyInfo(myInfo)
        }
    }
}