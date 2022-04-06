package app.i.cdms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.ChargeQrCode
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.NoticeList
import app.i.cdms.repository.home.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
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
    val myInfo = _myInfo.asStateFlow()
    private val _noticeList = MutableStateFlow<NoticeList?>(null)
    val noticeList = _noticeList.asStateFlow()
    private val _qrCode = MutableSharedFlow<ChargeQrCode?>()
    val qrCode = _qrCode.asSharedFlow()

    init {
        getMyInfo()
        getNotice()
    }

    fun getNotice() {
        viewModelScope.launch {
            _noticeList.value = homeRepository.getNotice()
        }
    }

    fun getMyInfo() {
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

    /**
     * 获取指定金额的充值二维码
     *
     * @param amount: 充值金额，单位分
     * @return
     */
    fun fetchChargeQrCode(amount: Int) {
        viewModelScope.launch {
            if (amount <= 0) {
                _qrCode.emit(null)
                return@launch
            }
            val result = homeRepository.fetchChargeQrCode(amount)
            result?.data?.let {
                _qrCode.emit(it)
            }
        }
    }
}