package info.note.app.server.websocket.usecase

import info.note.app.server.websocket.WebSocketMessageHandler
import io.ktor.websocket.Frame

class HandleWebSocketMessageUseCase(
    private val webSocketMessageHandler: WebSocketMessageHandler
) {

    suspend operator fun invoke(frame: Frame) = webSocketMessageHandler.onMessageFromClient(frame)
}