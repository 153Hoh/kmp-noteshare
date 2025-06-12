package info.note.app.feature.sync.repository

import info.note.app.feature.sync.model.CheckFilesResult
import info.note.app.feature.sync.model.DownloadResult
import info.note.app.feature.sync.model.ImageUploadResult
import info.note.app.feature.note.model.Note
import io.github.vinceglb.filekit.PlatformFile

interface SyncRepository {

    suspend fun connectToServer(ip: String): Result<Unit>

    suspend fun shouldSync(): Result<Unit>

    suspend fun sync(noteList: List<Note>): Result<List<Note>>

    suspend fun checkFileIds(fileIds: List<String>): Result<CheckFilesResult>

    suspend fun uploadFiles(uploadList: List<PlatformFile>): Result<ImageUploadResult>

    suspend fun downloadFiles(fileIds: List<String>, downloadFolder: String): Result<DownloadResult>
}