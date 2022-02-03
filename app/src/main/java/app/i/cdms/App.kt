package app.i.cdms

import android.app.Application
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import dagger.hilt.android.HiltAndroidApp

/**
 * @author ZZY
 * 2021/10/18.
 */
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (!AppCenter.isConfigured()) {
            AppCenter.start(
                this, "3b3f17f1-7367-49c1-a30f-c554e96333b4",
                Analytics::class.java, Crashes::class.java
            )
        }
    }
}