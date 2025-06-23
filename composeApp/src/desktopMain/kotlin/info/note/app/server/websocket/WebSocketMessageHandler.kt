package info.note.app.server.websocket

import info.note.app.feature.websocket.message.WebSocketMessage
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.SharedFlow

interface WebSocketMessageHandler {

    val sendMessageToClientFlow: SharedFlow<WebSocketMessage>

    suspend fun onMessageFromClient(frame: Frame)
    suspend fun sendMessageToClient(message: WebSocketMessage)
}