package app.i.cdms.ui.login

import android.util.Log
import android.util.Patterns
import androidx.datastore.preferences.protobuf.Api
import androidx.lifecycle.*
import app.i.cdms.data.LoginRepository
import app.i.cdms.data.Result

import app.i.cdms.R
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.data.model.Token
import app.i.cdms.repository.UserPrefRepository
import kotlinx.coroutines.launch
import kotlin.math.log

class LoginViewModel(
    private val loginRepository: LoginRepository,
    private val userPrefRepository: UserPrefRepository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<ApiResult<Token>>()
    val loginResult: LiveData<ApiResult<Token>> = _loginResult

    private val _captchaResult = MutableLiveData<ApiResult<CaptchaData>>()
    val captchaResult: LiveData<ApiResult<CaptchaData>> = _captchaResult

    fun getCaptcha() {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            val result = loginRepository.getCaptcha()

            if (result is Result.Success) {
                _captchaResult.value = result.data

//                if (result.data != null) {
//                    _captchaResult.value = result.data!!
//                } else {
//                    // TODO: 2021/10/19
//                    _captchaResult.value =
//                        ApiResult(code = 4000, data = null, msg = "result.data is null")
//                }
            } else {
                // TODO: 2021/10/20  
                _captchaResult.value = ApiResult(code = 4000, data = null, msg = "result is Error")
            }
        }
    }

    fun login(username: String, password: String, captcha: Int, uuid: String) {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            val result = loginRepository.login(username, password, captcha, uuid)
            if (result is Result.Success) {
                _loginResult.value = result.data
            } else {
                // TODO: 2021/10/20  
                _loginResult.value =
                    ApiResult(code = 123123, data = null, msg = "R.string.login_failed")
            }
        }
    }

    fun loginDataChanged(username: String, password: String, captcha: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (captcha.isBlank()) {
            _loginForm.value = LoginFormState(captchaError = R.string.invalid_captcha)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun updateToken(token: Token) {
        viewModelScope.launch {
            userPrefRepository.updateToken(token)
        }
    }
}