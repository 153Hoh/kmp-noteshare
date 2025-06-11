package info.note.app.domain.usecase

import info.note.app.domain.repository.file.FileRepository
import info.note.app.domain.repository.note.NoteRepository

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