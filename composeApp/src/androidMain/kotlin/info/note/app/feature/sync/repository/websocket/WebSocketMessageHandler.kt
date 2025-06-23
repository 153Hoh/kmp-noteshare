package info.note.app.feature.sync.repository.websocket

import info.note.app.feature.websocket.message.WebSocketMessage
import kotlinx.coroutines.flow.SharedFlow

interface WebSocketMessageHandler {

    val receiveMessageFromServer: SharedFlow<WebSocketMessage>

    suspend fun onMessageFromServer(message: WebSocketMessage)
}