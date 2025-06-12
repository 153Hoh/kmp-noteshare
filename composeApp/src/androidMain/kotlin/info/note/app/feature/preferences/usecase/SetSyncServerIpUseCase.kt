package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository

class SetSyncServerIpUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(ip: String) = preferencesRepository.setSyncServerIp(ip)
}