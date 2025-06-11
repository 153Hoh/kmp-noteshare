package info.note.app.domain.usecase

import info.note.app.domain.model.Note
import info.note.app.domain.repository.note.NoteRepository
import info.note.app.domain.repository.note.db.toNote

class FetchNoteDetailsUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String): Result<Note> =
        noteRepository.fetchNoteDetails(noteId).map { it.toNote() }
}