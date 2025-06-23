package info.note.app.server

import com.diamondedge.logging.logging
import info.note.app.server.routing.ServerRoutes
import io.ktor.serialization.gson.gson
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.partialcontent.PartialContent
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import kotlin.time.Duration.Companion.seconds

class SyncServerController(
    private val serverRoutes: ServerRoutes
) {

    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? =
        null

    fun start() {
        server = embeddedServer(CIO, port = 8080) {
            install(ContentNegotiation) {
                gson()
            }
            install(PartialContent)
            install(WebSockets) {
                pingPeriod = 15.seconds
                timeout = 15.seconds
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }

            routing {
                serverRoutes.syncRoutes(this)
                serverRoutes.websocket(this)
            }
        }.start()
        logging().info { "SyncServer started" }
    }

    fun stop() {
        server?.stop()
        logging().info { "SyncServer stopped" }
    }
}