package info.note.app.usecase

import info.note.app.Preferences

class RemoveSyncIpUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke() = preferences.setSyncServerIp("")
}