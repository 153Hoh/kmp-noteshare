package info.note.app.feature.sync.repository.websocket

import info.note.app.feature.websocket.message.WebSocketMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class WebSocketMessageHandlerImpl : WebSocketMessageHandler {

    private val _receiveMessageFromServer = MutableSharedFlow<WebSocketMessage>()
    override val receiveMessageFromServer = _receiveMessageFromServer.asSharedFlow()

    override suspend fun onMessageFromServer(message: WebSocketMessage) {
        _receiveMessageFromServer.emit(message)
    }
}