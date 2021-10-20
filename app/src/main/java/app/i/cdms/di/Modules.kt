package app.i.cdms.di

import app.i.cdms.api.ApiClient
import app.i.cdms.data.LoginDataSource
import app.i.cdms.data.LoginRepository
import app.i.cdms.data.db.AppDatabase
import app.i.cdms.repository.Repository
import app.i.cdms.ui.login.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.koin.dsl.single

/**
 * @author ZZY
 * 2021/10/18.
 */

val applicationScope = CoroutineScope(SupervisorJob())

val apiModule = module { single { ApiClient.create(get()) } }

val dbModule = module { single { AppDatabase.getDatabase(get(), applicationScope).dao() } }

val dataSourceModule = module {
    single <LoginDataSource>()
}

val repositoryModule = module {
    single { Repository(get(), get()) }
    single<LoginRepository>()
}

val preferenceModule = module {
//    single { androidApplication().getSharedPreferences(MAIN_STORAGE, Context.MODE_PRIVATE) }
//    single { androidApplication().getSharedPreferences(MAIN_STORAGE, Context.MODE_PRIVATE) }
//    val dataStorePref by lazy { DataStorePreferenceAdapter(dataStore, applicationScope)) }

}

val viewModelModule = module {
//    viewModel { GalleryViewModel(get()) }
//    viewModel { CategoriesViewModel(androidApplication(), get()) }
//    viewModel { DetailViewModel(get()) }
//    viewModel { FavoritesViewModel(get()) }
//    viewModel { VideosViewModel(get()) }

//    viewModel { LoginViewModel(get()) }
//    single<LoginDataSource>()
    viewModel<LoginViewModel>()
}
