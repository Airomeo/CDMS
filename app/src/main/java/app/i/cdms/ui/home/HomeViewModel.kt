package app.i.cdms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.i.cdms.data.model.ApiResult
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.Result
import app.i.cdms.repository.home.HomeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository
) : ViewModel() {
    private val _uiState = MutableSharedFlow<HomeUiState>()
    val uiState = _uiState.asSharedFlow()

    init {
        getMyInfo()
    }

    private fun getMyInfo() {
        viewModelScope.launch {
            delay(1)
            // can be launched in a separate asynchronous job
            _uiState.emit(HomeUiState.Loading)
            val result = homeRepository.getMyInfo()
            if (result is Result.Success) {
                when (result.data.code) {
                    200 -> {
                        result.data.data?.let {
                            _uiState.emit(HomeUiState.GetMyInfoSuccessful(it))
                            updateMyInfo(it)
                        }
                    }
                    else -> {
                        _uiState.emit(HomeUiState.GetMyInfoFailed(result.data))
                    }
                }
            } else if (result is Result.Error) {
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