package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository

class FetchSyncKeyUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke() = preferencesRepository.getSyncKey()
}