package info.note.app

import kotlinx.coroutines.flow.Flow

interface Preferences {

    suspend fun setLastSyncTime(time: Long)
    fun getLastSyncTime(): Flow<Long>

    suspend fun setLastSyncState(state: Boolean)
    fun getLastSyncState(): Flow<Boolean>

    suspend fun setSyncKey(key: String)
    suspend fun getSyncKey(): String
}