package info.note.app.feature.sync

import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.feature.websocket.message.WebSocketMessage
import info.note.app.server.websocket.WebSocketMessageHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SyncCoordinator(
    private val noteRepository: NoteRepository,
    private val preferencesRepository: PreferencesRepository,
    private val webSocketMessageHandler: WebSocketMessageHandler
) {

    fun init(scope: CoroutineScope) {
        scope.launch(Dispatchers.IO) {
            while (isActive) {
                if (shouldSync()) {
                    webSocketMessageHandler.sendMessageToClient(WebSocketMessage.SYNC)
                }
                delay(1000L)
            }
        }
    }

    private suspend fun shouldSync(): Boolean =
        System.currentTimeMillis() - noteRepository.lastUpdateTime() < 1000L
                || !preferencesRepository.getLastSyncState().first()
                || System.currentTimeMillis() - preferencesRepository.getLastSyncTime()
            .first() > 30_000L
}