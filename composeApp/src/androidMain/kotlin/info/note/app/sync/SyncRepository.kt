package info.note.app.sync

import info.note.app.domain.model.Note

interface SyncRepository {

    suspend fun connectToServer(ip: String): Result<Unit>

    suspend fun shouldSync(): Result<Unit>

    suspend fun sync(noteList: List<Note>): Result<List<Note>>
}