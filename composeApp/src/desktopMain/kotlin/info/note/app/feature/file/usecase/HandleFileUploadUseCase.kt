package info.note.app.feature.file.usecase

import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.file.repository.exception.FileSaveException
import info.note.app.feature.image.repository.exception.InvalidImageException
import io.ktor.http.content.PartData
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose

class HandleFileUploadUseCase(
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke(part: PartData, fileName: String?): Result<Pair<String, Boolean>> {
        if (part is PartData.FileItem) {
            val savedFileName = fileName ?: part.originalFileName ?: return Result.failure(
                FileSaveException()
            )

            fileRepository.createNewFileWithName(savedFileName).onSuccess {
                part.provider().copyAndClose(it.file.writeChannel())
                part.dispose()
                return Result.success(savedFileName to true)
            }.onFailure {
                return Result.success(savedFileName to false)
            }
        }
        return Result.failure(InvalidImageException())
    }
}