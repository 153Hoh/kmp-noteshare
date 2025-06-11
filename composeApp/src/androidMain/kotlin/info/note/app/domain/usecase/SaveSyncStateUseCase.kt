package info.note.app.domain.usecase

import info.note.app.Preferences

class SaveSyncStateUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke(state: Boolean) = preferences.setLastSyncState(state)
}