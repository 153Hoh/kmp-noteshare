package info.note.app.feature.sync.repository

interface NoteSyncController {

    suspend fun startSync()
    fun stopSync()
}