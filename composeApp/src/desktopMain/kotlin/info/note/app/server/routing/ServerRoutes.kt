package info.note.app.server.routing

import info.note.app.domain.model.ConnectResponseBody
import info.note.app.domain.model.SyncRequestBody
import info.note.app.domain.model.SyncResponseBody
import info.note.app.usecase.FetchSyncKeyUseCase
import info.note.app.usecase.HandleConnectUseCase
import info.note.app.usecase.SetLastSyncStateUseCase
import info.note.app.usecase.ShouldSyncUseCase
import info.note.app.usecase.SyncNotesUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

class ServerRoutes(
    private val syncNotesUseCase: SyncNotesUseCase,
    private val shouldSyncUseCase: ShouldSyncUseCase,
    private val setLastSyncStateUseCase: SetLastSyncStateUseCase,
    private val handleConnectUseCase: HandleConnectUseCase,
    private val fetchSyncKeyUseCase: FetchSyncKeyUseCase
) {

    fun syncRoutes(routingRoute: Route): Route {
        routingRoute.post("/sync") {
            val syncRequestBody = call.receive<SyncRequestBody>()

            val syncKey = fetchSyncKeyUseCase()

            if (call.request.headers[SYNC_KEY] != syncKey) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Sync key")
                return@post
            }

            syncNotesUseCase(syncRequestBody.noteList).onSuccess {
                setLastSyncStateUseCase(true)
                call.response.headers.append(SYNC_KEY, syncKey)
                call.respond(SyncResponseBody(it))
            }.onFailure {
                setLastSyncStateUseCase(false)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        routingRoute.get("/connect") {
            val syncKey = fetchSyncKeyUseCase()

            if (syncKey.isNotEmpty()) {
                call.respond(HttpStatusCode.MethodNotAllowed, "Already connected")
                return@get
            }

            val mewSyncKey = handleConnectUseCase()
            call.respond(ConnectResponseBody(mewSyncKey))
        }
        routingRoute.get("/shouldSync") {
            val syncKey = fetchSyncKeyUseCase()

            if (call.request.headers[SYNC_KEY] != syncKey) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Sync key")
                return@get
            }

            if (shouldSyncUseCase()) {
                call.response.headers.append(SYNC_KEY, syncKey)
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.BadRequest)
            }
        }
        return routingRoute
    }

    companion object {
        private const val SYNC_KEY = "syncKey"
    }
}