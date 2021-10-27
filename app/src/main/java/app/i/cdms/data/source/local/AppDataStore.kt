package app.i.cdms.data.source.local

import android.content.Context
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.preferencesDataStore
import app.i.cdms.Constant

/**
 * @author ZZY
 * 2021/10/27.
 */
class AppDataStore(context: Context) {
    private val Context.dataStore by preferencesDataStore(
        name = Constant.USER_PREFERENCES_NAME,
        produceMigrations = { context ->
            // Since we're migrating from SharedPreferences, add a migration based on the
            // SharedPreferences name
            listOf(SharedPreferencesMigration(context, Constant.USER_PREFERENCES_NAME))
        }
    )
    val dataStore = context.dataStore
}