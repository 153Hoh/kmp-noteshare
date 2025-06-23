package info.note.app.feature.sync.repository.websocket

import com.diamondedge.logging.logging
import info.note.app.feature.websocket.message.WebSocketMessage
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class WebSocketControllerImpl(
    private val webSocketMessageHandler: WebSocketMessageHandler
) : WebSocketController {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val webSocketClient = HttpClient {
        install(WebSockets) {
            pingIntervalMillis = 15_000
        }
    }

    private var isConnected = false

    override fun start(serverIp: String) {
        if (isConnected) {
            return
        }

        scope.launch {
            webSocketClient.webSocket(
                method = HttpMethod.Get,
                host = serverIp,
                port = 8080,
                path = "/ws"
            ) {
                isConnected = true
                incoming.consumeEach {
                    runCatching {
                        if (it is Frame.Text) {
                            logging().info { "Message received: ${it.readText()}" }
                            val message = WebSocketMessage.valueOf(it.readText())
                            webSocketMessageHandler.onMessageFromServer(message)
                        }
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }
        }
    }

    override fun stop() {
        isConnected = false
        scope.cancel()
    }
}