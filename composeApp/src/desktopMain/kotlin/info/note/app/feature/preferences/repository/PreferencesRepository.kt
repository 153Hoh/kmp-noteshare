package info.note.app.feature.preferences.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    suspend fun setLastSyncTime(time: Long)
    fun getLastSyncTime(): Flow<Long>

    suspend fun setLastSyncState(state: Boolean)
    fun getLastSyncState(): Flow<Boolean>

    suspend fun setSyncKey(key: String)
    suspend fun getSyncKey(): String

    fun getRawThemeState(): Flow<String>
    suspend fun setRawThemeState(rawThemeState: String)
}