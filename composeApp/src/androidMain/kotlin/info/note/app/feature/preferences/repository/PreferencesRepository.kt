package info.note.app.feature.preferences.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    suspend fun setSyncServerIp(ip: String)
    suspend fun getSyncServerIp(): String

    suspend fun setLastSyncState(state: Boolean)
    fun getLastSyncState(): Flow<Boolean>

    suspend fun setLastSyncTime(time: Long)
    fun getLastSyncTime(): Flow<Long>

    suspend fun setSyncKey(key: String)
    suspend fun getSyncKey(): String
}