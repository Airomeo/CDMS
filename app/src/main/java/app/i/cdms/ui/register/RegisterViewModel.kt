package app.i.cdms.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.model.RegisterFormState
import app.i.cdms.data.model.YiDaBaseResponse
import app.i.cdms.repository.register.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _registerForm = MutableStateFlow<RegisterFormState?>(null)
    val registerFormState = _registerForm.asStateFlow()

    private val _registerResult = MutableSharedFlow<YiDaBaseResponse<Any>?>()
    val registerResult = _registerResult.asSharedFlow()

    fun register(username: String, password: String, phone: String) {
        viewModelScope.launch {
            val result = registerRepository.register(username, password, phone)
            _registerResult.emit(result)
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
}