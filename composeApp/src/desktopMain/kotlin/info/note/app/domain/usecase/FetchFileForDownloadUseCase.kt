package info.note.app.domain.usecase

import info.note.app.domain.repository.file.FileRepository
import java.io.File

class FetchFileForDownloadUseCase(
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke(fileId: String): Result<File> =
        fileRepository.fetchFileById(fileId).map { it.file }
}