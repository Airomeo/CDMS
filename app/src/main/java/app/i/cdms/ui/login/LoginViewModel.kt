package app.i.cdms.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.model.CaptchaData
import app.i.cdms.data.model.Token
import app.i.cdms.repository.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _captchaData = MutableStateFlow<CaptchaData?>(null)
    val captchaData = _captchaData.asStateFlow()

    init {
        getCaptcha()
    }

    fun getCaptcha() {
        viewModelScope.launch {
            val result = loginRepository.getCaptcha()
            _captchaData.value = result?.data
        }
    }

    fun login(username: String, password: String, captcha: Int) {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
            val uuid = captchaData.value?.uuid ?: return@launch
            val result = loginRepository.login(username, password, captcha, uuid)
            if (result?.data == null) {
                _uiState.emit(LoginUiState.LoginFailed)
            } else {
                _uiState.emit(LoginUiState.LoginSuccessful(result.data))
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
    data class LoginSuccessful(val token: Token) : LoginUiState()
    object LoginFailed : LoginUiState()
}