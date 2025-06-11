package info.note.app.domain.usecase

import info.note.app.Preferences

class FetchLastSyncTimeUseCase(
    private val preferences: Preferences
) {

    operator fun invoke() = preferences.getLastSyncTime()
}