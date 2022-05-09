package app.i.cdms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.model.ChargeQrCode
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.NoticeList
import app.i.cdms.repository.AuthRepository
import app.i.cdms.repository.home.HomeRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _myInfo = MutableStateFlow<MyInfo?>(null)
    val myInfo = _myInfo.asStateFlow()
    private val _noticeList = MutableStateFlow<NoticeList?>(null)
    val noticeList = _noticeList.asStateFlow()
    private val _qrCode = MutableSharedFlow<ChargeQrCode?>()
    val qrCode = _qrCode.asSharedFlow()
    private val _refreshing = MutableSharedFlow<Boolean>()
    val refreshing = _refreshing.asSharedFlow()


    init {
        getMyInfo()
        getNotice()
    }

    fun getNotice() {
        viewModelScope.launch {
            _refreshing.emit(true)
            _noticeList.value = homeRepository.getNotice()
            _refreshing.emit(false)
        }
    }

    fun getMyInfo() {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            _refreshing.emit(true)
            val result = homeRepository.getMyInfo()
            _myInfo.value = result?.data
            _refreshing.emit(false)
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

    /**
     * 更改密码
     * {"code":200,"msg":"操作成功","data":null}
     * {"code":500,"msg":"修改密码失败，旧密码错误","data":null}
     * @param old: old password
     * @param new: new password
     * @return
     */
    fun updatePassword(old: String, new: String) {
        viewModelScope.launch {
            val result = authRepository.updatePassword(old, new)
            if (result?.data == 200) {
                EventBus.produceEvent(BaseEvent.Toast(R.string.sign_up_success))
            }
        }
    }
}