package info.note.app.usecase

import info.note.app.Preferences

class SetLastSyncStateUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke(state: Boolean) = preferences.setLastSyncState(state)
}