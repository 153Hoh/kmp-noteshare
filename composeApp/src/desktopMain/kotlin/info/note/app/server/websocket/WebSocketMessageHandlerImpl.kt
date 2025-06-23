package info.note.app.server.websocket

import com.diamondedge.logging.logging
import info.note.app.feature.websocket.message.WebSocketMessage
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WebSocketMessageHandlerImpl : WebSocketMessageHandler {

    private val _sendMessageToClientFlow = MutableSharedFlow<WebSocketMessage>()
    override val sendMessageToClientFlow = _sendMessageToClientFlow.asSharedFlow()

    override suspend fun onMessageFromClient(frame: Frame) {
        logging().info { "Message from client: $frame" }
    }

    override suspend fun sendMessageToClient(message: WebSocketMessage) {
        _sendMessageToClientFlow.emit(message)
    }
}