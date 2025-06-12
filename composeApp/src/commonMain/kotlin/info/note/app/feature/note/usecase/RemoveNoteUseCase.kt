package info.note.app.feature.note.usecase

import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.note.repository.NoteRepository

class RemoveNoteUseCase(
    private val noteRepository: NoteRepository,
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke(noteId: String): Result<Unit> {
        noteRepository.fetchNoteDetails(noteId).onSuccess {
            if (it.imageId.isNotEmpty()) {
                fileRepository.removeFileById(it.imageId)
            }
        }
        return noteRepository.removeNote(noteId)
    }
}