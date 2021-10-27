package app.i.cdms

import android.app.Application
import app.i.cdms.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

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
}