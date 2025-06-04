package info.note.app.sync

import info.note.app.Preferences
import info.note.app.domain.model.ConnectResponseBody
import info.note.app.domain.model.Note
import info.note.app.domain.model.SyncRequestBody
import info.note.app.domain.model.SyncResponseBody
import info.note.app.sync.exception.InvalidResponseException
import info.note.app.sync.exception.NotConnectedException
import info.note.app.sync.exception.ServerErrorException
import info.note.app.sync.exception.ServerUnavailableException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KtorSyncRepository(
    private val preferences: Preferences
) : SyncRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    override suspend fun connectToServer(ip: String): Result<Unit> = withContext(Dispatchers.IO) {
        val response = client.get("http://$ip:8080/connect")
        return@withContext if (response.status.value in 200..299) {
            val connectResponse = response.body<ConnectResponseBody>()
            preferences.setSyncKey(connectResponse.syncKey)
            Result.success(Unit)
        } else {
            Result.failure(ServerUnavailableException())
        }
    }

    override suspend fun shouldSync(): Result<Unit> = withContext(Dispatchers.IO) {
        val serverIp = preferences.getSyncServerIp()
        val syncKey = preferences.getSyncKey()

        if (syncKey.isEmpty()) {
            return@withContext Result.failure(NotConnectedException())
        }

        if (serverIp.isEmpty()) {
            return@withContext Result.failure(ServerUnavailableException())
        }

        runCatching {
            val response = client.get("http://$serverIp:8080/shouldSync") {
                url {
                    headers.append(SYNC_KEY, syncKey)
                }
            }

            if (response.headers[SYNC_KEY] != syncKey) {
                return@withContext Result.failure(InvalidResponseException())
            }

            return@withContext if (response.status.value in 200..299) {
                Result.success(Unit)
            } else {
                Result.failure(ServerUnavailableException())
            }
        }.onFailure {
            return@withContext Result.failure(ServerErrorException())
        }
    }

    override suspend fun sync(noteList: List<Note>): Result<List<Note>> =
        withContext(Dispatchers.IO) {
            val serverIp = preferences.getSyncServerIp()
            val syncKey = preferences.getSyncKey()

            if (syncKey.isEmpty()) {
                return@withContext Result.failure(NotConnectedException())
            }

            if (serverIp.isEmpty()) {
                return@withContext Result.failure(ServerUnavailableException())
            }

            runCatching {
                val response = client.post("http://$serverIp:8080/sync") {
                    url {
                        headers.append(SYNC_KEY, syncKey)
                    }
                    contentType(ContentType.Application.Json)
                    setBody(SyncRequestBody(noteList = noteList))
                }

                if (response.headers[SYNC_KEY] != syncKey) {
                    return@withContext Result.failure(InvalidResponseException())
                }

                return@withContext if (response.status.value in 200..299) {
                    val syncResponseBody = response.body<SyncResponseBody>()
                    Result.success(syncResponseBody.noteList)
                } else {
                    Result.failure(ServerErrorException())
                }
            }.onFailure {
                return@withContext Result.failure(ServerErrorException())
            }
        }

    companion object {
        private const val SYNC_KEY = "syncKey"
    }
}