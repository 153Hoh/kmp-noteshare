package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class FetchLastSyncStateUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke(): Flow<Boolean> = preferencesRepository.getLastSyncState()
}