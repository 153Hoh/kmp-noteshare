package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository

class SetLastSyncStateUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(state: Boolean) = preferencesRepository.setLastSyncState(state)
}