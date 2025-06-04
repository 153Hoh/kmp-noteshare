package info.note.app.usecase

import info.note.app.Preferences
import info.note.app.domain.model.Note
import info.note.app.sync.SyncRepository

class SyncNotesUseCase(
    private val syncRepository: SyncRepository,
    private val preferences: Preferences
) {

    suspend operator fun invoke(noteList: List<Note>): Result<List<Note>> {
        preferences.setLastSyncTime(System.currentTimeMillis())
        return syncRepository.sync(noteList)
    }
}