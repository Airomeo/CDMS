package app.i.cdms.data.source.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.i.cdms.Constant
import app.i.cdms.data.model.Area
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.MyInfoJsonAdapter
import app.i.cdms.data.model.Token
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton


/**
 * @author ZZY
 * 2021/10/21.
 */
@Singleton
class UserPrefDataSource @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore by preferencesDataStore(
        name = Constant.USER_PREFERENCES_NAME,
        produceMigrations = { context ->
            // Since we're migrating from SharedPreferences, add a migration based on the
            // SharedPreferences name
            listOf(SharedPreferencesMigration(context, Constant.USER_PREFERENCES_NAME))
        }
    )

    private object PreferencesKeys {
        // TODO 加密存储
        val SORT_ORDER = stringPreferencesKey(Constant.PREF_SORT_ORDER)
        val SHOW_COMPLETED = booleanPreferencesKey(Constant.PREF_SHOW_COMPLETED)
        val PREF_TOKEN = stringPreferencesKey(Constant.PREF_TOKEN)
        val PREF_MY_INFO = stringPreferencesKey(Constant.PREF_MY_INFO)
        val PREF_AREA_LIST = stringPreferencesKey(Constant.PREF_AREA_LIST)
        val PREF_SELECTED_THEME = stringPreferencesKey(Constant.PREF_SELECTED_THEME)
        val PREF_CONFERENCE_TIME_ZONE = booleanPreferencesKey(Constant.PREF_CONFERENCE_TIME_ZONE)
    }

    val tokenFlow: Flow<Token> = context.dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                Log.e("TAG", "UserPrefDataSource: \"Error reading PREF_TOKEN.\"", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            Token(preferences[PreferencesKeys.PREF_TOKEN] ?: "")
        }

    val myInfoFlow: Flow<MyInfo?> = context.dataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            Log.e("TAG", "UserPrefDataSource: \"Error reading PREF_MY_INFO.\"", exception)
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[PreferencesKeys.PREF_MY_INFO]?.let {
            kotlin.runCatching {
                MyInfoJsonAdapter(Moshi.Builder().build()).fromJson(it)
            }.onFailure {
                Log.e("TAG", "PREF_MY_INFO.", it)
            }.getOrNull()
        }
    }

    val areaListFlow: Flow<List<Area>?> = context.dataStore.data.catch { exception ->
        // dataStore.data throws an IOException when an error is encountered when reading data
        if (exception is IOException) {
            Log.e("TAG", "UserPrefDataSource: \"Error reading PREF_AREA_LIST.\"", exception)
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }.map { preferences ->
        preferences[PreferencesKeys.PREF_AREA_LIST]?.let {
            val type: Type = Types.newParameterizedType(
                List::class.java,
                Area::class.java
            )
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<List<Area>> = moshi.adapter(type)

            kotlin.runCatching {
                jsonAdapter.fromJson(it)
            }.onFailure {
                Log.e("TAG", "PREF_AREA_LIST.", it)
            }.getOrNull()
        }
    }

    suspend fun updateToken(token: Token) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_TOKEN] = token.token
        }
    }

    suspend fun updateMyInfo(myInfo: MyInfo) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PREF_MY_INFO] =
                MyInfoJsonAdapter(Moshi.Builder().build()).toJson(myInfo)
        }
    }

    suspend fun updateAreaList(areaList: List<Area>) {
        context.dataStore.edit { preferences ->
            val type: Type = Types.newParameterizedType(
                List::class.java,
                Area::class.java
            )
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<List<Area>> = moshi.adapter(type)
            preferences[PreferencesKeys.PREF_AREA_LIST] = jsonAdapter.toJson(areaList)
        }
    }
}