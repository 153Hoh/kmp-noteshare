package info.note.app.sync

interface NoteSyncController {

    suspend fun startSync()
    fun stopSync()
}