package info.note.app.domain.usecase

import info.note.app.Preferences

class FetchSyncKeyUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke() = preferences.getSyncKey()
}