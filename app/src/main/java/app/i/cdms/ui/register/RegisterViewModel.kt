package app.i.cdms.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.Result
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.RegisterFormState
import app.i.cdms.data.model.Token
import app.i.cdms.repository.UserPrefRepository
import app.i.cdms.repository.login.LoginRepository
import app.i.cdms.repository.register.RegisterRepository
import app.i.cdms.ui.login.LoginFormState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult = MutableLiveData<ApiResult<Any>>()
    val registerResult: LiveData<ApiResult<Any>> = _registerResult

    fun register(token: Token?, username: String, password: String, phone: String) {
        viewModelScope.launch {
            if (token != null) {
                val result = registerRepository.register(token, username, password, phone)
                if (result is Result.Success) {
                    _registerResult.value = result.data
                } else {
                    // TODO: 2021/10/20
                    _registerResult.value =
                        ApiResult(code = 999, data = null, msg = "R.string.Result.Error")
                }
            } else {
                _registerResult.value =
                    ApiResult(code = 999, data = null, msg = "Token is null")
                return@launch
            }
        }
    }

    fun registerDataChanged(username: String, password: String, phone: String) {
        if (!isUserNameValid(username)) {
            _registerForm.value = RegisterFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else if (phone.length != 11) {
            _registerForm.value = RegisterFormState(phoneError = R.string.invalid_phone)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
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
    // TODO: Implement the ViewModel
}