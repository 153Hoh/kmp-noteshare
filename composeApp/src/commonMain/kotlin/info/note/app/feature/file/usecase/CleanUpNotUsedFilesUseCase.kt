package info.note.app.feature.file.usecase

import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.note.repository.NoteRepository

class CleanUpNotUsedFilesUseCase(
    private val noteRepository: NoteRepository,
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke() {
        noteRepository
            .getAllNotes().onSuccess { notes ->
                val usedFileIds = notes.filter { it.imageId.isNotEmpty() }.map { it.imageId }

                fileRepository.fetchImageFileIds().onSuccess { cachedFileIds ->
                    val notUsedFileIds = cachedFileIds.filterNot { usedFileIds.contains(it) }
                    fileRepository.deleteNotUsedFiles(notUsedFileIds)
                }
            }
    }
}