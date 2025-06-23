package info.note.app.server.websocket.usecase

import info.note.app.server.websocket.WebSocketMessageHandler
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchWebSocketMessagesToClientUseCase(
    private val webSocketMessageHandler: WebSocketMessageHandler
) {

    operator fun invoke(): Flow<Frame> =
        webSocketMessageHandler.sendMessageToClientFlow.map { Frame.Text(it.name) }
}