package app.i.cdms.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import app.i.cdms.Constant
import app.i.cdms.R
import app.i.cdms.data.model.Area
import app.i.cdms.data.model.MyInfo
import app.i.cdms.data.model.MyInfoJsonAdapter
import app.i.cdms.data.model.Token
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
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
class UserPrefDataSource @Inject constructor(private val dataStore: DataStore<Preferences>) {
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

    val tokenFlow: Flow<Token?> = dataStore.safeRead(PreferencesKeys.PREF_TOKEN) {
        Token(it)
    }

    val myInfoFlow: Flow<MyInfo?> = dataStore.safeRead(PreferencesKeys.PREF_MY_INFO) {
        kotlin.runCatching {
            MyInfoJsonAdapter(Moshi.Builder().build()).fromJson(it)
        }.getOrNull()
    }

    val areaListFlow: Flow<List<Area>?> = dataStore.safeRead(PreferencesKeys.PREF_AREA_LIST) {
        val type: Type = Types.newParameterizedType(
            List::class.java,
            Area::class.java
        )
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<List<Area>> = moshi.adapter(type)

        kotlin.runCatching {
            jsonAdapter.fromJson(it)
        }.getOrNull()
    }

    suspend fun updateToken(token: Token?) {
        dataStore.edit { preferences ->
            if (token == null) {
                preferences.remove(PreferencesKeys.PREF_TOKEN)
            } else {
                preferences[PreferencesKeys.PREF_TOKEN] = token.token
            }
        }
    }

    suspend fun updateMyInfo(myInfo: MyInfo?) {
        dataStore.edit { preferences ->
            if (myInfo == null) {
                preferences.remove(PreferencesKeys.PREF_MY_INFO)
            } else {
                preferences[PreferencesKeys.PREF_MY_INFO] =
                    MyInfoJsonAdapter(Moshi.Builder().build()).toJson(myInfo)
            }
        }
    }

    suspend fun updateAreaList(areaList: List<Area>) {
        dataStore.edit { preferences ->
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

inline fun <reified T, R> DataStore<Preferences>.safeRead(
    key: Preferences.Key<T>,
    crossinline transform: (T) -> R
) =
    data.catch { exception ->
        when (exception) {
            is IOException -> emit(emptyPreferences())
            is NullPointerException -> {
                throw exception
            }// do something
            else -> throw exception
        }
    }.map { preference ->
        preference[key]?.let {
            transform.invoke(it)
        }
    }