package app.i.cdms.ui.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.R
import app.i.cdms.data.model.RegisterFormState
import app.i.cdms.data.model.Result
import app.i.cdms.repository.register.RegisterRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository
) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _uiState = MutableSharedFlow<RegisterUiState>()
    val uiState = _uiState.asSharedFlow()

    fun register(username: String, password: String, phone: String) {
        viewModelScope.launch {
            _uiState.emit(RegisterUiState.Loading)
            val result = registerRepository.register(username, password, phone)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            _uiState.emit(RegisterUiState.RegisterSuccess)
                        }
                    }
                    else -> {
                        _uiState.emit(RegisterUiState.Error(Exception(result.data.msg)))
//                        Token is null
                    }
                }
            } else if (result is Result.Error) {
                _uiState.emit(RegisterUiState.Error(result.exception))
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

sealed class RegisterUiState {
    object Loading : RegisterUiState()
    object RegisterSuccess : RegisterUiState()
    data class Error(val exception: Throwable) : RegisterUiState()
    object None : RegisterUiState()
}