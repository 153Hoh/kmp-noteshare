package info.note.app.feature.sync.repository.websocket

interface WebSocketController {

    fun start(serverIp: String)
    fun stop()
}