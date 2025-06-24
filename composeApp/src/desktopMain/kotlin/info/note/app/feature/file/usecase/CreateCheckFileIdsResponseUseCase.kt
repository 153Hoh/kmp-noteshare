package info.note.app.feature.file.usecase

import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.sync.model.CheckFilesResponseBody

class CreateCheckFileIdsResponseUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(fileIds: List<String>): Result<CheckFilesResponseBody> =
        noteRepository
            .getAllNotes()
            .map { notes ->
                val ownFileIds = notes.filter { it.imageId.isNotEmpty() }.map { it.imageId }

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