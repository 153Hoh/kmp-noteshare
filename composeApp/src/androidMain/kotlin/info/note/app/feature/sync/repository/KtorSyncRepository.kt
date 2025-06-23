package info.note.app.feature.sync.repository

import info.note.app.feature.note.model.Note
import info.note.app.feature.sync.model.CheckFilesRequestBody
import info.note.app.feature.sync.model.CheckFilesResponseBody
import info.note.app.feature.sync.model.CheckFilesResult
import info.note.app.feature.sync.model.ConnectResponseBody
import info.note.app.feature.sync.model.DownloadResult
import info.note.app.feature.sync.model.ImageUploadResult
import info.note.app.feature.sync.model.SyncRequestBody
import info.note.app.feature.sync.model.SyncResponseBody
import info.note.app.feature.sync.model.UploadRequest
import info.note.app.feature.sync.model.UploadResponseBody
import info.note.app.feature.sync.repository.exception.InvalidResponseException
import info.note.app.feature.sync.repository.exception.NotConnectedException
import info.note.app.feature.sync.repository.exception.ServerErrorException
import info.note.app.feature.sync.repository.exception.ServerUnavailableException
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class KtorSyncRepository : SyncRepository {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    override suspend fun connectToServer(ip: String): Result<String> = withContext(Dispatchers.IO) {
        val response = client.get("http://$ip:8080/connect")
        return@withContext if (response.status.value in 200..299) {
            val connectResponse = response.body<ConnectResponseBody>()
            Result.success(connectResponse.syncKey)
        } else {
            Result.failure(ServerUnavailableException())
        }
    }

    override suspend fun sync(
        noteList: List<Note>,
        serverIp: String,
        syncKey: String
    ): Result<List<Note>> =
        withContext(Dispatchers.IO) {
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

    override suspend fun checkFileIds(
        fileIds: List<String>,
        serverIp: String,
        syncKey: String
    ): Result<CheckFilesResult> =
        withContext(Dispatchers.IO) {
            if (syncKey.isEmpty()) {
                return@withContext Result.failure(NotConnectedException())
            }

            if (serverIp.isEmpty()) {
                return@withContext Result.failure(ServerUnavailableException())
            }

            runCatching {
                val response = client.post("http://$serverIp:8080/checkFileIds") {
                    url {
                        headers.append(SYNC_KEY, syncKey)
                    }
                    contentType(ContentType.Application.Json)
                    setBody(CheckFilesRequestBody(fileIds))
                }

                if (response.headers[SYNC_KEY] != syncKey) {
                    return@withContext Result.failure(InvalidResponseException())
                }

                return@withContext if (response.status.value in 200..299) {
                    val checkFilesResponseBody = response.body<CheckFilesResponseBody>()
                    Result.success(CheckFilesResult.from(checkFilesResponseBody))
                } else {
                    Result.failure(ServerErrorException())
                }

            }.onFailure {
                return@withContext Result.failure(ServerErrorException())
            }
        }

    override suspend fun uploadFiles(
        uploadList: List<PlatformFile>,
        serverIp: String
    ): Result<ImageUploadResult> =
        withContext(Dispatchers.IO) {
            if (serverIp.isEmpty()) {
                return@withContext Result.failure(ServerUnavailableException())
            }

            val uploadRequestList = uploadList.map { UploadRequest(it.name, it.readBytes()) }

            runCatching {
                val response = client.post("http://$serverIp:8080/uploadImage") {
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                uploadRequestList.forEach { uploadRequest ->
                                    append("image", uploadRequest.byteArray, Headers.build {
                                        append(
                                            HttpHeaders.ContentDisposition,
                                            "filename=\"${uploadRequest.name}\""
                                        )
                                    })
                                }
                            }
                        ))
                }

                return@withContext if (response.status.value in 200..299) {
                    val uploadResponseBody = response.body<UploadResponseBody>()
                    Result.success(ImageUploadResult.from(uploadResponseBody))
                } else {
                    Result.failure(ServerErrorException())
                }

            }.onFailure {
                return@withContext Result.failure(ServerErrorException())
            }
        }

    override suspend fun downloadFiles(
        fileIds: List<String>,
        downloadFolder: String,
        serverIp: String
    ): Result<DownloadResult> =
        withContext(Dispatchers.IO) {
            if (serverIp.isEmpty()) {
                return@withContext Result.failure(ServerUnavailableException())
            }

            val downloadResults = mutableMapOf<String, Boolean>()
            fileIds.forEach { fileId ->
                runCatching {
                    val response = client.get("http://$serverIp:8080/download") {
                        url {
                            parameters.append("fileId", fileId)
                        }
                    }

                    if (response.status.value in 200..299) {
                        val fileBytes = response.body<ByteArray>()
                        File(downloadFolder, fileId).apply {
                            writeBytes(fileBytes)
                        }
                        downloadResults[fileId] = true
                    } else {
                        downloadResults[fileId] = false
                    }
                }.onFailure {
                    it.printStackTrace()
                    downloadResults[fileId] = false
                }
            }
            return@withContext Result.success(DownloadResult(downloadResults))
        }


    companion object {
        private const val SYNC_KEY = "syncKey"
    }
}