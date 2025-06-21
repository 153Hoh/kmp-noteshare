package info.note.app.feature.file.usecase

import info.note.app.feature.file.repository.FileRepository
import java.io.File

class FetchFileForDownloadUseCase(
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke(fileId: String): Result<File> =
        fileRepository.fetchFileById(fileId).map { it.file }
}