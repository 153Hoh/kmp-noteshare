package info.note.app.usecase

import info.note.app.Preferences

class SetSyncServerIpUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke(ip: String) = preferences.setSyncServerIp(ip)
}