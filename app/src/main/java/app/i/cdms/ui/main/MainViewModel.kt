package app.i.cdms.ui.main

import android.util.Log
import androidx.lifecycle.*
import app.i.cdms.data.Result
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyInfo
import app.i.cdms.repository.UserPrefRepository
import app.i.cdms.repository.main.MainRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(
    private val mainRepository: MainRepository,
    private val userPrefRepository: UserPrefRepository
) : ViewModel() {

    val token = userPrefRepository.tokenFlow.asLiveData()
    private val _myInfo = MutableLiveData<ApiResult<MyInfo>>()
    val myInfo: LiveData<ApiResult<MyInfo>> = _myInfo

    fun getMyInfo() {
        viewModelScope.launch {
            val tokenString = token.value
            if (tokenString == null) {
                return@launch
            } else {
                // can be launched in a separate asynchronous job
                val result = mainRepository.getMyInfo(tokenString)
                if (result is Result.Success) {
                    _myInfo.value = result.data
                } else {
                    // TODO: 2021/10/20
                    _myInfo.value =
                        ApiResult(code = 123123, data = null, msg = "R.string.getMyInfoFailed")
                }
            }
        }
    }

    fun updateMyInfo(myInfo: MyInfo) {
        viewModelScope.launch {
            userPrefRepository.updateMyInfo(myInfo)
        }
    }

    fun verifyToken() {
        viewModelScope.launch {
            token.value?.token
        }
    }
}