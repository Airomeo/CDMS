package app.i.cdms

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import app.i.cdms.di.*
import app.i.cdms.repository.UserPrefRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * @author ZZY
 * 2021/10/18.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Koin Android logger
            androidLogger()
            //inject Android context
            androidContext(this@App)
            // use modules
            modules(
                listOf(
                    apiModule,
                    dbModule,
                    dataSourceModule,
                    repositoryModule,
                    preferenceModule,
                    viewModelModule
                )
            )

        }

    }

    private val Context.dataStore by preferencesDataStore(
        name = Constant.USER_PREFERENCES_NAME,
        produceMigrations = { context ->
            // Since we're migrating from SharedPreferences, add a migration based on the
            // SharedPreferences name
            listOf(SharedPreferencesMigration(context, Constant.USER_PREFERENCES_NAME))
        }
    )
}