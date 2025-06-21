package info.note.app.server.routing

import info.note.app.feature.sync.model.CheckFilesRequestBody
import info.note.app.feature.sync.model.ConnectResponseBody
import info.note.app.feature.sync.model.SyncRequestBody
import info.note.app.feature.sync.model.SyncResponseBody
import info.note.app.feature.sync.model.UploadResponseBody
import info.note.app.feature.file.usecase.CreateCheckFileIdsResponseUseCase
import info.note.app.feature.file.usecase.FetchFileForDownloadUseCase
import info.note.app.feature.preferences.usecase.FetchSyncKeyUseCase
import info.note.app.feature.preferences.usecase.HandleConnectUseCase
import info.note.app.feature.file.usecase.HandleFileUploadUseCase
import info.note.app.feature.preferences.usecase.SetLastSyncStateUseCase
import info.note.app.feature.note.usecase.ShouldSyncUseCase
import info.note.app.feature.note.usecase.SyncNotesUseCase
import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

class ServerRoutes(
    private val syncNotesUseCase: SyncNotesUseCase,
    private val shouldSyncUseCase: ShouldSyncUseCase,
    private val setLastSyncStateUseCase: SetLastSyncStateUseCase,
    private val handleConnectUseCase: HandleConnectUseCase,
    private val fetchSyncKeyUseCase: FetchSyncKeyUseCase,
    private val createCheckFileIdsResponseUseCase: CreateCheckFileIdsResponseUseCase,
    private val handleFileUploadUseCase: HandleFileUploadUseCase,
    private val fetchFileForDownloadUseCase: FetchFileForDownloadUseCase
) {

    fun syncRoutes(routingRoute: Route): Route = routingRoute.apply {
        post("/sync") {
            val syncKey = fetchSyncKeyUseCase()

            if (call.request.headers[SYNC_KEY] != syncKey) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Sync key")
                return@post
            }

            val requestBody = call.receive<SyncRequestBody>()

            syncNotesUseCase(requestBody.noteList).onSuccess {
                setLastSyncStateUseCase(true)
                call.response.headers.append(SYNC_KEY, syncKey)
                call.respond(SyncResponseBody(it))
            }.onFailure {
                setLastSyncStateUseCase(false)
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
        post("/checkFileIds") {
            val syncKey = fetchSyncKeyUseCase()

            if (call.request.headers[SYNC_KEY] != syncKey) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Sync key")
                return@post
            }

            val requestBody = call.receive<CheckFilesRequestBody>()

            createCheckFileIdsResponseUseCase(requestBody.fileIds).onSuccess {
                call.response.headers.append(SYNC_KEY, syncKey)
                call.respond(it)
            }.onFailure {
                call.respond(it)
            }
        }
        post("/uploadImage") {
            val multipartData = call.receiveMultipart(formFieldLimit = 1024 * 1024 * 100)

            val resultMap = mutableMapOf<String, Boolean>()

            multipartData.forEachPart {
                val fileName =
                    it.headers[HttpHeaders.ContentDisposition]
                        .takeIf { cd -> cd.isNullOrEmpty() }
                        ?.let { contentDisposition ->
                            ContentDisposition.parse(contentDisposition).parameter("filename")
                        }
                handleFileUploadUseCase(it, fileName).onSuccess { result ->
                    resultMap[result.first] = result.second
                }
            }

            call.respond(UploadResponseBody(resultMap))
        }
        get("/download") {
            val fileId = call.request.queryParameters["fileId"]
            if (fileId == null) {
                call.respond(HttpStatusCode.BadRequest)
                return@get
            }

            fetchFileForDownloadUseCase(fileId).onSuccess {
                call.respondFile(it)
            }.onFailure {
                it.printStackTrace()
                call.respond(it)
            }
        }
        get("/connect") {
            val syncKey = fetchSyncKeyUseCase()

            if (syncKey.isNotEmpty()) {
                call.respond(HttpStatusCode.MethodNotAllowed, "Already connected")
                return@get
            }

            val mewSyncKey = handleConnectUseCase()
            call.respond(ConnectResponseBody(mewSyncKey))
        }
        get("/shouldSync") {
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
    }

    companion object {
        private const val SYNC_KEY = "syncKey"
    }
}