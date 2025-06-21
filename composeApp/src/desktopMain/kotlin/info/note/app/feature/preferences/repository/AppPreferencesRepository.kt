package info.note.app.feature.preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toOkioPath
import java.io.File

class AppPreferencesRepository : PreferencesRepository {

    private val datastore: DataStore<androidx.datastore.preferences.core.Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                File(
                    System.getProperty("java.io.tmpdir"),
                    "note.preferences_pb"
                ).toOkioPath()
            }
        )

    override suspend fun setLastSyncTime(time: Long) {
        datastore.edit { settings ->
            settings[longPreferencesKey(LAST_SYNC_TIME)] = time
        }
    }

    override fun getLastSyncTime(): Flow<Long> =
        datastore.data.map { settings -> settings[longPreferencesKey(LAST_SYNC_TIME)] ?: 0L }

    override suspend fun setLastSyncState(state: Boolean) {
        datastore.edit { settings ->
            settings[booleanPreferencesKey(LAST_SYNC_STATE)] = state
        }
    }

    override fun getLastSyncState(): Flow<Boolean> =
        datastore.data.map { settings ->
            settings[booleanPreferencesKey(LAST_SYNC_STATE)] ?: false
        }

    override suspend fun setSyncKey(key: String) {
        datastore.edit { settings ->
            settings[stringPreferencesKey(SYNC_KEY)] = key
        }
    }

    override suspend fun getSyncKey(): String =
        datastore.data.map { settings -> settings[stringPreferencesKey(SYNC_KEY)] ?: "" }
            .first()

    override fun getRawThemeState(): Flow<String> =
        datastore.data.map { settings -> settings[stringPreferencesKey(THEME_STATE)] ?: "" }

    override suspend fun setRawThemeState(rawThemeState: String) {
        datastore.edit { settings ->
            settings[stringPreferencesKey(THEME_STATE)] = rawThemeState
        }
    }

    companion object {
        private const val LAST_SYNC_TIME = "lastSyncTime"
        private const val LAST_SYNC_STATE = "lastSyncState"
        private const val SYNC_KEY = "syncKey"
        private const val THEME_STATE = "themeState"
    }
}