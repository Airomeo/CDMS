package app.i.cdms.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import app.i.cdms.Constant
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.MyInfoJsonAdapter
import app.i.cdms.data.model.Token
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


/**
 * @author ZZY
 * 2021/10/21.
 */
class UserPrefRepository(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        // TODO 加密存储
        val SORT_ORDER = stringPreferencesKey(Constant.PREF_SORT_ORDER)
        val SHOW_COMPLETED = booleanPreferencesKey(Constant.PREF_SHOW_COMPLETED)
        val PREF_TOKEN = stringPreferencesKey(Constant.PREF_TOKEN)
        val PREF_MY_INFO = stringPreferencesKey(Constant.PREF_MY_INFO)
        val PREF_SELECTED_THEME = stringPreferencesKey(Constant.PREF_SELECTED_THEME)
        val PREF_CONFERENCE_TIME_ZONE = booleanPreferencesKey(Constant.PREF_CONFERENCE_TIME_ZONE)
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