package info.note.app.feature.file.repository

import info.note.app.feature.image.model.ImageResult
import io.github.vinceglb.filekit.PlatformFile

interface FileRepository {

    val parentFolder: String

    suspend fun loadImageFile(fileId: String): Result<ImageResult>

    suspend fun createNewFileWithName(fileName: String): Result<PlatformFile>

    suspend fun fetchFileById(fileId: String): Result<PlatformFile>

    suspend fun fetchImageFileIds(): Result<List<String>>

    suspend fun cacheImageFile(fileToCache: String, newFileName: String): Result<String>

    suspend fun removeFileById(fileId: String): Result<Unit>

    suspend fun deleteNotUsedFiles(usedFileIdList: List<String>): Result<Unit>

    suspend fun deleteAllFiles(): Result<Unit>
}