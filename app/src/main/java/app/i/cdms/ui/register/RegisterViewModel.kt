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
import app.i.cdms.utils.BaseEvent
import app.i.cdms.utils.EventBus
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
            EventBus.produceEvent(BaseEvent.Loading)
            val result = registerRepository.register(username, password, phone)
            EventBus.produceEvent(BaseEvent.None)
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            _uiState.emit(RegisterUiState.RegisterSuccess)
                        }
                    }
                    else -> {
                        EventBus.produceEvent(BaseEvent.Failed(result.data))
                    }
                }
            } else if (result is Result.Error) {
                EventBus.produceEvent(BaseEvent.Error(result.exception))
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
    object RegisterSuccess : RegisterUiState()
}