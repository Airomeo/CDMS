package app.i.cdms.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Result
import app.i.cdms.repository.home.HomeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {

    //    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.None)
//    val uiState = _uiState.asStateFlow()
    private val _uiState = MutableSharedFlow<HomeUiState>()
    val uiState = _uiState.asSharedFlow()

    init {
        Log.d("TAG", "HomeViewModel: init getMyInfo")
        getMyInfo()
    }

    private fun getMyInfo() {
        viewModelScope.launch {
            // can be launched in a separate asynchronous job
//            _uiState.value = HomeUiState.Loading
            _uiState.emit(HomeUiState.Loading)
            val result = homeRepository.getMyInfo()
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
//                            _uiState.value = HomeUiState.GetMyInfoSuccessful(it)
                            _uiState.emit(HomeUiState.GetMyInfoSuccessful(it))
                            updateMyInfo(it)
                        }
                    }
                    else -> {
//                        _uiState.value = HomeUiState.GetMyInfoFailed(result.data)
                        _uiState.emit(HomeUiState.GetMyInfoFailed(result.data))
                    }
                }
            } else if (result is Result.Error) {
//                _uiState.value = HomeUiState.Error(result.exception)
                _uiState.emit(HomeUiState.Error(result.exception))
            }
        }
    }

    private fun updateMyInfo(myInfo: MyInfo) {
        viewModelScope.launch {
            homeRepository.updateMyInfo(myInfo)
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class GetMyInfoSuccessful(val myInfo: MyInfo) : HomeUiState()
    data class GetMyInfoFailed(val apiResult: ApiResult<Any>) : HomeUiState()
    data class Error(val exception: Throwable) : HomeUiState()
    object None : HomeUiState()
}