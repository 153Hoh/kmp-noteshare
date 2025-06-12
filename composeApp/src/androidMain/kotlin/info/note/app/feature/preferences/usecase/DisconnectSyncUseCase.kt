package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository

class DisconnectSyncUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke() {
        with(preferencesRepository) {
            setLastSyncState(false)
            setSyncKey("")
            setSyncServerIp("")
            setLastSyncTime(0L)
        }
    }
}