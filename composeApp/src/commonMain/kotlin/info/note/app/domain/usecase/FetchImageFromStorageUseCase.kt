package info.note.app.domain.usecase

import info.note.app.domain.repository.file.FileRepository
import info.note.app.domain.repository.image.ImageResult

class FetchImageFromStorageUseCase(
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke(fileId: String): Result<ImageResult> =
        fileRepository.loadImageFile(fileId)
}