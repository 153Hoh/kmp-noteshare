package info.note.app.feature.sync.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.feature.sync.repository.SyncRepository
import info.note.app.feature.sync.repository.websocket.WebSocketController

class CheckAndConnectToServerUseCase(
    private val syncRepository: SyncRepository,
    private val preferencesRepository: PreferencesRepository,
    private val webSocketController: WebSocketController
) {

    suspend operator fun invoke(ip: String): Result<Unit> =
        syncRepository
            .connectToServer(ip)
            .map { syncKey ->
                with(preferencesRepository) {
                    setSyncServerIp(ip)
                    setSyncKey(syncKey)
                }
                webSocketController.start(ip)
            }
}