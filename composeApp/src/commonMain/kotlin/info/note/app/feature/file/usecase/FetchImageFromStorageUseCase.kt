package info.note.app.feature.file.usecase

import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.image.model.ImageResult

class FetchImageFromStorageUseCase(
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke(fileId: String): Result<ImageResult> =
        fileRepository.loadImageFile(fileId)
}