package app.i.cdms.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.MyInfoJsonAdapter
import app.i.cdms.data.model.Token
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import com.squareup.moshi.Moshi


/**
 * @author ZZY
 * 2021/10/21.
 */
class UserPrefRepository(private val dataStore: DataStore<Preferences>) {
    private object PreferencesKeys {
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val SHOW_COMPLETED = booleanPreferencesKey("show_completed")

        // TODO 加密存储
        val PREF_TOKEN = stringPreferencesKey("pref_token")
        val PREF_MY_INFO = stringPreferencesKey("pref_my_info")
        val PREF_SELECTED_THEME = stringPreferencesKey("pref_dark_mode")
        val PREF_CONFERENCE_TIME_ZONE = booleanPreferencesKey("pref_conference_time_zone")

    }

    val tokenFlow: Flow<Token?> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("TAG", "UserPrefRepository: \"Error reading PREF_TOKEN.\"", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[PreferencesKeys.PREF_TOKEN]?.let {
                Token(it)
            }
        }

    val myInfoFlow: Flow<MyInfo?> = dataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            Log.e("TAG", "UserPrefRepository: \"Error reading PREF_MY_INFO.\"", exception)
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[PreferencesKeys.PREF_MY_INFO]?.let {
            MyInfoJsonAdapter(Moshi.Builder().build()).fromJson(it)
        }
    }

    suspend fun updateToken(token: Token) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_TOKEN] = token.token
        }
    }

    suspend fun updateMyInfo(myInfo: MyInfo) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_MY_INFO] =
                MyInfoJsonAdapter(Moshi.Builder().build()).toJson(myInfo)
        }
    }
}