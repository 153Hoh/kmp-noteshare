package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository
import java.util.UUID

class HandleConnectUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(): String {
        val syncKey = UUID.randomUUID().toString()
        preferencesRepository.setSyncKey(syncKey)
        return syncKey
    }
}