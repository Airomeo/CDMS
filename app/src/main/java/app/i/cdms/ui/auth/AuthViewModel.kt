package app.i.cdms.ui.auth

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.CountDownTimer
import android.util.Base64
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.repository.AuthRepository
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val mainRepository: MainRepository
) : ViewModel() {

    // 重置密码弹窗显示状态
    var openDialog by mutableStateOf(false)

    // Lord的邀请码
    private var lordInviteCode: String? = null

    // 登录或注册UI状态
    var isSignIn by mutableStateOf(true)

    // 验证码倒计时
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

    //    private val _captchaData = MutableStateFlow<CaptchaData?>(null)
    private var _captchaData: CaptchaData? = null
    var captchaBitmap by mutableStateOf<Bitmap?>(null)

    init {
        fetchLoginCaptcha()
    }

    fun fetchLoginCaptcha() {
        viewModelScope.launch {
            val result = authRepository.fetchLoginCaptcha()
//            _captchaData.value = result?.data
            _captchaData = result?.data
            captchaBitmap = if (result?.data == null) {
                null
            } else {
                val imageByteArray =
                    Base64.decode(result.data.imgBytes, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(
                    imageByteArray,
                    0,
                    imageByteArray.size
                )
            }
        }
    }

    fun login(username: String, password: String, captcha: Int) {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
//            val uuid = _captchaData.value?.uuid ?: return@launch
            val uuid = _captchaData?.uuid ?: return@launch
            val result = authRepository.login(username, password, captcha, uuid)
            if (result?.data == null) {
                authRepository.fetchLoginCaptcha()
            } else {
                mainRepository.updateToken(result.data)
            }
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
            lordInviteCode = lordInviteCode ?: mainRepository.fetchLordInviteCode2()?.data
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
                isSignIn = true // 跳转登录界面
                EventBus.produceEvent(BaseEvent.Toast(R.string.sign_up_success))
            }
        }
    }

    /**
     * 重置密码
     * {"code":200,"msg":"重置密码已发送至您的手机，请尽快前往个人中心重置，避免遗忘","data":null}
     * {"code":500,"msg":"未找到当前手机号绑定的易达账户，请确认手机号是否准确","data":null}
     * @param phone: phone number
     * @return
     */
    fun retrievePassword(phone: String) {
        viewModelScope.launch {
            val result = authRepository.retrievePassword(phone)
            if (result?.data == 200) {
                EventBus.produceEvent(BaseEvent.Toast(R.string.retrieve_password_success))
                openDialog = false
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
        }
    }
}