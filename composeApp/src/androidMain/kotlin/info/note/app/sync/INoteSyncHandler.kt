package info.note.app.sync

interface INoteSyncHandler {

    suspend fun startSync()
    fun stopSync()
}