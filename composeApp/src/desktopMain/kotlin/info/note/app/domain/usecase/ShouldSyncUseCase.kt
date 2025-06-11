package info.note.app.domain.usecase

import info.note.app.Preferences
import info.note.app.domain.repository.note.NoteRepository
import kotlinx.coroutines.flow.first

class ShouldSyncUseCase(
    private val noteRepository: NoteRepository,
    private val preferences: Preferences
) {

    suspend operator fun invoke(): Boolean =
        System.currentTimeMillis() - noteRepository.lastUpdateTime() < 1000L
                || !preferences.getLastSyncState().first()
                || System.currentTimeMillis() - preferences.getLastSyncTime().first() > 30_000L
}