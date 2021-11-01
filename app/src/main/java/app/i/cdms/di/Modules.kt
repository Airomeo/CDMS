package app.i.cdms.di

import app.i.cdms.api.ApiClient
import app.i.cdms.data.source.local.AppDataStore
import app.i.cdms.data.source.local.AppDatabase
import app.i.cdms.data.source.remote.login.LoginDataSource
import app.i.cdms.data.source.remote.main.MainDataSource
import app.i.cdms.data.source.remote.register.RegisterDataSource
import app.i.cdms.data.source.remote.team.TeamDataSource
import app.i.cdms.repository.UserPrefRepository
import app.i.cdms.repository.login.LoginRepository
import app.i.cdms.repository.main.MainRepository
import app.i.cdms.repository.register.RegisterRepository
import app.i.cdms.repository.team.TeamRepository
import app.i.cdms.ui.agent.AgentViewModel
import app.i.cdms.ui.home.HomeViewModel
import app.i.cdms.ui.login.LoginViewModel
import app.i.cdms.ui.main.MainViewModel
import app.i.cdms.ui.notifications.NotificationsViewModel
import app.i.cdms.ui.register.RegisterViewModel
import app.i.cdms.ui.team.TeamViewModel
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

val apiModule = module {
    single { ApiClient.create(androidContext()) }
}

val dbModule = module {
    single { AppDatabase.getDatabase(get(), applicationScope).dao() }
}

val dataSourceModule = module {
    single<MainDataSource>()
    single<LoginDataSource>()
    single<RegisterDataSource>()
    single<TeamDataSource>()
}

val repositoryModule = module {
    single<MainRepository>()
    single<LoginRepository>()
    single<RegisterRepository>()
    single<TeamRepository>()
    single<UserPrefRepository>()
}

val preferenceModule = module {
    single { AppDataStore(androidContext()).dataStore }
}

val viewModelModule = module {
    viewModel<LoginViewModel>()
    viewModel<HomeViewModel>()
    viewModel<MainViewModel>()
    viewModel<RegisterViewModel>()
    viewModel<TeamViewModel>()
    viewModel<NotificationsViewModel>()
    viewModel<AgentViewModel>()
}
