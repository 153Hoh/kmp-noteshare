package info.note.app.feature.sync.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.sync.repository.SyncRepository
import kotlinx.coroutines.flow.first

class ShouldSyncUseCase(
    private val noteRepository: NoteRepository,
    private val syncRepository: SyncRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(): Boolean =
        System.currentTimeMillis() - noteRepository.lastUpdateTime() < 1000L
                || syncRepository.shouldSync().isSuccess
                || !preferencesRepository.getLastSyncState().first()
                || System.currentTimeMillis() - preferencesRepository.getLastSyncTime().first() > 30000L
}