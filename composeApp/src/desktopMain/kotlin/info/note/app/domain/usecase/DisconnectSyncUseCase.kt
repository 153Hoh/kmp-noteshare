package info.note.app.domain.usecase

import info.note.app.Preferences

class DisconnectSyncUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke() {
        with(preferences) {
            setLastSyncState(false)
            setSyncKey("")
            setLastSyncTime(0L)
        }
    }
}