package app.i.cdms

import android.app.Activity
import android.app.Application
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.distribute.Distribute
import com.microsoft.appcenter.distribute.DistributeListener
import com.microsoft.appcenter.distribute.ReleaseDetails
import com.microsoft.appcenter.distribute.UpdateAction
import dagger.hilt.android.HiltAndroidApp


/**
 * @author ZZY
 * 2021/10/18.
 */
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Distribute.setListener(object : DistributeListener {
            override fun onReleaseAvailable(
                activity: Activity,
                releaseDetails: ReleaseDetails
            ): Boolean {
                // Build our own dialog title and message
                val dialogBuilder = MaterialAlertDialogBuilder(activity)

                dialogBuilder.setTitle(R.string.appcenter_distribute_update_dialog_title) // you should use a string resource instead, this is just a simple example
                dialogBuilder.setMessage(releaseDetails.releaseNotes)

                // Mimic default SDK buttons
                dialogBuilder.setPositiveButton(R.string.appcenter_distribute_update_dialog_download) { dialog, which ->
                    Distribute.notifyUpdateAction(UpdateAction.UPDATE)
                }

                // We can postpone the release only if the update isn't mandatory
                if (!releaseDetails.isMandatoryUpdate) {
                    dialogBuilder.setNegativeButton(R.string.appcenter_distribute_update_dialog_postpone) { dialog, which ->
                        // This method is used to tell the SDK what button was clicked
                        Distribute.notifyUpdateAction(UpdateAction.POSTPONE)
                    }
                }
                dialogBuilder.setCancelable(false) // if it's cancelable you should map cancel to postpone, but only for optional updates
                dialogBuilder.create().show()

                // Return true if you're using your own dialog, false otherwise
                return true
            }

            override fun onNoReleaseAvailable(activity: Activity) {}
        })

        if (!AppCenter.isConfigured()) {
            AppCenter.start(
                this, "3b3f17f1-7367-49c1-a30f-c554e96333b4",
                Analytics::class.java, Crashes::class.java, Distribute::class.java
            )
        }
    }
}