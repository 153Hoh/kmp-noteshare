package info.note.app.usecase

import info.note.app.Preferences

class SaveSyncStateUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke(state: Boolean) = preferences.setLastSyncState(state)
}