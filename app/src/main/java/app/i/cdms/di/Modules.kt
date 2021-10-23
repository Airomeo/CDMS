package app.i.cdms.di

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import app.i.cdms.Constant
import app.i.cdms.api.ApiClient
import app.i.cdms.data.remote.login.LoginDataSource
import app.i.cdms.repository.login.LoginRepository
import app.i.cdms.data.db.AppDatabase
import app.i.cdms.data.remote.main.MainDataSource
import app.i.cdms.repository.Repository
import app.i.cdms.repository.UserPrefRepository
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.ui.home.HomeViewModel
import app.i.cdms.ui.login.LoginViewModel
import app.i.cdms.ui.main.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.single

/**
 * @author ZZY
 * 2021/10/18.
 */

val applicationScope = CoroutineScope(SupervisorJob())

private val Context.dataStore by preferencesDataStore(
    name = Constant.USER_PREFERENCES_NAME,
//    produceMigrations = { context ->
//        // Since we're migrating from SharedPreferences, add a migration based on the
//        // SharedPreferences name
//        listOf(SharedPreferencesMigration(context, Constant.USER_PREFERENCES_NAME))
//    }
)

val apiModule = module { single { ApiClient.create(get()) } }

val dbModule = module {
    single { AppDatabase.getDatabase(get(), applicationScope).dao() }
}

val dataSourceModule = module {
    single <MainDataSource>()
    single <LoginDataSource>()
}

val repositoryModule = module {
    single { Repository(get(), get()) }
    single<MainRepository>()
    single<LoginRepository>()
    single<UserPrefRepository>()
}

val preferenceModule = module {
//    single { androidApplication().getSharedPreferences(MAIN_STORAGE, Context.MODE_PRIVATE) }
    single { androidContext().dataStore }
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
    viewModel<HomeViewModel>()
    viewModel<MainViewModel>()
}
