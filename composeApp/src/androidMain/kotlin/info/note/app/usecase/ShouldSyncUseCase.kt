package info.note.app.usecase

import info.note.app.Preferences
import info.note.app.domain.repository.NoteRepository
import info.note.app.sync.SyncRepository
import kotlinx.coroutines.flow.first

class ShouldSyncUseCase(
    private val noteRepository: NoteRepository,
    private val syncRepository: SyncRepository,
    private val preferences: Preferences
) {

    suspend operator fun invoke(): Boolean =
        System.currentTimeMillis() - noteRepository.lastUpdateTime() < 1000L || syncRepository.shouldSync().isSuccess || !preferences.getLastSyncState().first()
}