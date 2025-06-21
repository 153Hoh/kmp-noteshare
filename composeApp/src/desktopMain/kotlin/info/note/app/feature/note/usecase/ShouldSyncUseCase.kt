package info.note.app.feature.note.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.feature.note.repository.NoteRepository
import kotlinx.coroutines.flow.first

class ShouldSyncUseCase(
    private val noteRepository: NoteRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(): Boolean =
        System.currentTimeMillis() - noteRepository.lastUpdateTime() < 1000L
                || !preferencesRepository.getLastSyncState().first()
                || System.currentTimeMillis() - preferencesRepository.getLastSyncTime().first() > 30_000L
}