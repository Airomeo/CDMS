package app.i.cdms.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.data.model.Result
import app.i.cdms.data.model.Token
import app.i.cdms.repository.login.LoginRepository
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _uiState = MutableSharedFlow<LoginUiState>()
    val uiState = _uiState.asSharedFlow()
    private var captchaData: CaptchaData? = null

    init {
        getCaptcha()
    }

    fun getCaptcha() {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            EventBus.produceEvent(BaseEvent.Loading)
            val result = loginRepository.getCaptcha()
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            EventBus.produceEvent(BaseEvent.None)
                            captchaData = it
                            _uiState.emit(LoginUiState.GetCaptchaSuccessful(it))
                        }
                    }
                    401 -> {
                        EventBus.produceEvent(BaseEvent.NeedLogin)
                    }
                    else -> {
                        EventBus.produceEvent(BaseEvent.None)
                        _uiState.emit(LoginUiState.GetCaptchaFailed(result.data))
                    }
                }
            } else if (result is Result.Error) {
                EventBus.produceEvent(BaseEvent.Error(result.exception))
            }
        }
    }

    fun login(username: String, password: String, captcha: Int) {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            val uuid = captchaData?.uuid ?: return@launch
            EventBus.produceEvent(BaseEvent.Loading)
            val result = loginRepository.login(username, password, captcha, uuid)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            EventBus.produceEvent(BaseEvent.None)
                            _uiState.emit(LoginUiState.LoginSuccessful(it))
                        }
                    }
                    401 -> {
                        EventBus.produceEvent(BaseEvent.NeedLogin)
                    }
                    else -> {
                        EventBus.produceEvent(BaseEvent.None)
                        _uiState.emit(LoginUiState.LoginFailed(result.data))
                    }
                }
            } else if (result is Result.Error) {
                EventBus.produceEvent(BaseEvent.Error(result.exception))
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
}

sealed class LoginUiState {
    data class GetCaptchaSuccessful(val captchaData: CaptchaData) : LoginUiState()
    data class GetCaptchaFailed(val apiResult: ApiResult<Any>) : LoginUiState()
    data class LoginSuccessful(val token: Token) : LoginUiState()
    data class LoginFailed(val apiResult: ApiResult<Any>) : LoginUiState()
}