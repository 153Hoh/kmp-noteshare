package info.note.app.feature.sync.repository

import info.note.app.feature.note.model.Note
import info.note.app.feature.sync.model.CheckFilesResult
import info.note.app.feature.sync.model.DownloadResult
import info.note.app.feature.sync.model.ImageUploadResult
import io.github.vinceglb.filekit.PlatformFile

interface SyncRepository {

    suspend fun connectToServer(ip: String): Result<String>

    suspend fun sync(noteList: List<Note>, serverIp: String, syncKey: String): Result<List<Note>>

    suspend fun checkFileIds(
        fileIds: List<String>,
        serverIp: String,
        syncKey: String
    ): Result<CheckFilesResult>

    suspend fun uploadFiles(
        uploadList: List<PlatformFile>,
        serverIp: String
    ): Result<ImageUploadResult>

    suspend fun downloadFiles(
        fileIds: List<String>,
        downloadFolder: String,
        serverIp: String
    ): Result<DownloadResult>
}