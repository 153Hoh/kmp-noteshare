package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository

class FetchLastSyncTimeUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke() = preferencesRepository.getLastSyncTime()
}