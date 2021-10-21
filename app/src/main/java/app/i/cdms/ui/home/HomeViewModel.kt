package app.i.cdms.ui.home

import androidx.lifecycle.*
import app.i.cdms.repository.UserPrefRepository
import kotlinx.coroutines.launch

class HomeViewModel(userPrefRepository: UserPrefRepository) : ViewModel() {

    private val token = userPrefRepository.tokenFlow.asLiveData()
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = token.map {
        it?.token ?: "Null"
    }

    fun verifyToken() {
        viewModelScope.launch {
            token.value?.token
        }
    }
}