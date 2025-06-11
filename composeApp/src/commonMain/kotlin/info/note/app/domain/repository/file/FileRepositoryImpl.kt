package info.note.app.domain.repository.file

import info.note.app.Platform
import info.note.app.domain.repository.image.ImageResult
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.copyTo
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.list
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException

class FileRepositoryImpl(
    private val platform: Platform
) : FileRepository {

    override val parentFolder: String
        get() = platform.filesDir.absolutePath()

    override suspend fun cacheImageFile(fileToCache: String, newFileName: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                val file = PlatformFile(fileToCache)
                val cachedLocation = PlatformFile(platform.filesDir, newFileName)

                file.copyTo(cachedLocation)

                return@runCatching cachedLocation.absolutePath()
            }
        }

    override suspend fun removeFileById(fileId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                fetchFileById(fileId).map { file ->
                    file.delete()
                }.getOrThrow()
            }
        }

    override suspend fun deleteAllNotes(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            platform.filesDir
                .list()
                .filter { it.name.endsWith("-noteImage") }
                .forEach { it.delete() }
        }
    }

    override suspend fun loadImageFile(fileId: String): Result<ImageResult> =
        withContext(Dispatchers.IO) {
            runCatching {
                val file = platform.filesDir.list().find { it.name == fileId }
                    ?: throw FileNotFoundException()

                return@runCatching ImageResult(
                    fileId = fileId,
                    path = file.absolutePath(),
                    bitmap = file.toImageBitmap()
                )
            }
        }

    override suspend fun createNewFileWithName(fileName: String): Result<PlatformFile> =
        withContext(Dispatchers.IO) {
            runCatching {
                return@runCatching PlatformFile(platform.filesDir, fileName)
            }
        }

    override suspend fun fetchFileById(fileId: String): Result<PlatformFile> =
        withContext(Dispatchers.IO) {
            runCatching {
                return@runCatching platform.filesDir.list().find { it.name == fileId }
                    ?: throw FileNotFoundException()
            }
        }

    override suspend fun fetchImageFileIds(): Result<List<String>> =
        withContext(Dispatchers.IO) {
            runCatching {
                return@runCatching platform.filesDir
                    .list()
                    .filter { it.name.endsWith("-noteImage") }
                    .map { it.name }
            }
        }
}