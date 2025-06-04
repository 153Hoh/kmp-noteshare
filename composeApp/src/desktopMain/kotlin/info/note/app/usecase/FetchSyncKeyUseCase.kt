package info.note.app.usecase

import info.note.app.Preferences

class FetchSyncKeyUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke() = preferences.getSyncKey()
}