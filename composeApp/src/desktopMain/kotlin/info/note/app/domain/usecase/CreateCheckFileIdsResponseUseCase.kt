package info.note.app.domain.usecase

import info.note.app.domain.model.CheckFilesResponseBody
import info.note.app.domain.repository.file.FileRepository

class CreateCheckFileIdsResponseUseCase(
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke(fileIds: List<String>): Result<CheckFilesResponseBody> =
        fileRepository.fetchImageFileIds().map { ownFileIds ->
            val downloadList = ownFileIds.filterNot { fileIds.contains(it) }
            val uploadList = fileIds.filterNot { ownFileIds.contains(it) }
            return Result.success(
                CheckFilesResponseBody(
                    downloadList,
                    uploadList
                )
            )
        }
}