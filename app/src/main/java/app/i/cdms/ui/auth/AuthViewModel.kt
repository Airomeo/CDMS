package app.i.cdms.ui.auth

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.repository.AuthRepository
import app.i.cdms.repository.main.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mainRepository: MainRepository
) : ViewModel() {
    // Lord的邀请码
    private var lordInviteCode: String? = null

    // 注册结果状态
    var registerState by mutableStateOf(false)
    var countDownTime by mutableStateOf(0L)

    // 获取短信验证码倒计时10分钟
    private val timer = object : CountDownTimer(600000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            countDownTime = millisUntilFinished
        }

        override fun onFinish() {
            countDownTime = 0
        }
    }

    /**
     * 用户注册时如果没有填写邀请码，用我的邀请码
     *
     * @param :
     * @return {"code":200,"msg":"成功","data":"YD002354NK1H"} or {"code":500,"msg":"不允许重复提交，请稍候再试","data":null}
     */
    fun fetchLordInviteCode() {
        viewModelScope.launch {
            val result = mainRepository.fetchLordInviteCode()
            if (result?.code == 500) {
//                fetchLordInviteCode()
            } else {
                lordInviteCode = result?.data
            }
        }
    }

    fun fetchRegisterCaptcha(phone: String) {
        viewModelScope.launch {
            val result = authRepository.fetchRegisterCaptcha(phone)
            if (result?.code == 200) timer.start()
        }
    }

    /**
     * 注册接口
     *
     * @param username: String
     * @param password: String
     * @param phone: String
     * @param phoneCaptcha: String
     * @param inviteCode: String
     * @return
     */
    fun register(
        username: String,
        password: String,
        phone: String,
        phoneCaptcha: String,
        inviteCode: String
    ) {
        viewModelScope.launch {
            // 重试两次
            lordInviteCode = lordInviteCode ?: mainRepository.fetchLordInviteCode()?.data
            lordInviteCode = lordInviteCode ?: mainRepository.fetchLordInviteCode()?.data
            if (lordInviteCode == null && inviteCode.isBlank()) return@launch
            val result =
                authRepository.register(
                    username,
                    password,
                    phone,
                    phoneCaptcha,
                    inviteCode.ifBlank { lordInviteCode!! }
                )
            if (result?.code == 200) {
                registerState = true
            }
        }
    }
}