package info.note.app.feature.sync.usecase

import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.preferences.repository.PreferencesRepository
import kotlinx.coroutines.flow.first

class ShouldSyncUseCase(
    private val noteRepository: NoteRepository,
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(): Boolean =
        enoughTimePassedSinceLastSync()
                && (isNoteRepositoryUpdated()
                || !isLastSuccessFul()
                || isLastSyncTimeHigherThanMaxDelay())

    private fun isNoteRepositoryUpdated() =
        System.currentTimeMillis() - noteRepository.lastUpdateTime() < 1000L

    private suspend fun isLastSuccessFul() = preferencesRepository.getLastSyncState().first()

    private suspend fun isLastSyncTimeHigherThanMaxDelay() =
        System.currentTimeMillis() - preferencesRepository.getLastSyncTime().first() > 30000L

    private suspend fun enoughTimePassedSinceLastSync() =
        System.currentTimeMillis() - preferencesRepository.getLastSyncTime().first() > 1000L
}