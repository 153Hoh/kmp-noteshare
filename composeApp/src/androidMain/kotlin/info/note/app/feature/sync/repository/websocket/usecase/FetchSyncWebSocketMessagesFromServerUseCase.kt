package info.note.app.feature.sync.repository.websocket.usecase

import info.note.app.feature.sync.repository.websocket.WebSocketMessageHandler
import info.note.app.feature.websocket.message.WebSocketMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

class FetchSyncWebSocketMessagesFromServerUseCase(
    private val webSocketMessageHandler: WebSocketMessageHandler
) {

    operator fun invoke(): Flow<WebSocketMessage> =
        webSocketMessageHandler
            .receiveMessageFromServer
            .filter { it == WebSocketMessage.SYNC }
}