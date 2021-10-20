package app.i.cdms.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.i.cdms.api.ApiClient
import app.i.cdms.api.ApiService
import app.i.cdms.data.LoginDataSource
import app.i.cdms.data.LoginRepository

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
class LoginViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
//            return LoginViewModel(
//                loginRepository = LoginRepository(
//                    dataSource = LoginDataSource(ApiClient.create(get()))
//                )
//            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}