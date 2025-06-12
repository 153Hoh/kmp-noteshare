package info.note.app.feature.preferences.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AppPreferencesRepository(
    private val context: Context
) : PreferencesRepository {

    private val Context.datastore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
        name = "noteShare"
    )

    override suspend fun setSyncServerIp(ip: String) {
        context.datastore.edit { settings ->
            settings[stringPreferencesKey(SYNC_SERVER_IP)] = ip
        }
    }

    override suspend fun getSyncServerIp(): String =
        context.datastore.data.map { settings ->
            settings[stringPreferencesKey(SYNC_SERVER_IP)] ?: ""
        }
            .first()

    override suspend fun setLastSyncState(state: Boolean) {
        context.datastore.edit { settings ->
            settings[booleanPreferencesKey(LAST_SYNC_STATE)] = state
        }
    }

    override fun getLastSyncState(): Flow<Boolean> =
        context.datastore.data.map { settings ->
            settings[booleanPreferencesKey(LAST_SYNC_STATE)] ?: false
        }

    override suspend fun setLastSyncTime(time: Long) {
        context.datastore.edit { settings ->
            settings[longPreferencesKey(LAST_SYNC_TIME)] = time
        }
    }

    override fun getLastSyncTime(): Flow<Long> =
        context.datastore.data.map { settings -> settings[longPreferencesKey(LAST_SYNC_TIME)] ?: 0L }

    override suspend fun setSyncKey(key: String) {
        context.datastore.edit { settings ->
            settings[stringPreferencesKey(SYNC_KEY)] = key
        }
    }

    override suspend fun getSyncKey(): String =
        context.datastore.data.map { settings -> settings[stringPreferencesKey(SYNC_KEY)] ?: "" }
            .first()



    companion object {
        private const val SYNC_SERVER_IP = "syncServerIp"
        private const val LAST_SYNC_STATE = "lastSyncState"
        private const val SYNC_KEY = "syncKey"
        private const val LAST_SYNC_TIME = "lastSyncTime"
    }
}