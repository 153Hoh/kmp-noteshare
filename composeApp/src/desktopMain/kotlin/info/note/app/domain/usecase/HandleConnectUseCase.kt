package info.note.app.domain.usecase

import info.note.app.Preferences
import java.util.UUID

class HandleConnectUseCase(
    private val preferences: Preferences
) {

    suspend operator fun invoke(): String {
        val syncKey = UUID.randomUUID().toString()
        preferences.setSyncKey(syncKey)
        return syncKey
    }
}