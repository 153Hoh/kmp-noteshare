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
import io.ktor.server.routing.routing

class SyncServer(
    private val serverRoutes: ServerRoutes
) {

    private var server: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>? =
        null

    fun start() {
        server = embeddedServer(CIO, port = 8080) {
            install(ContentNegotiation) {
                gson()
            }

            routing {
                serverRoutes.syncRoutes(this)
            }
        }.start()
        logging().info { "SyncServer started" }
    }

    fun stop() {
        server?.stop()
        logging().info { "SyncServer stopped" }
    }
}