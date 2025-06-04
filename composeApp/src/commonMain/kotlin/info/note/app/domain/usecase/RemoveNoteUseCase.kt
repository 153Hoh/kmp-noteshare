package info.note.app.domain.usecase

import info.note.app.domain.repository.NoteRepository

class RemoveNoteUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String): Result<Unit> = noteRepository.removeNote(noteId)
}