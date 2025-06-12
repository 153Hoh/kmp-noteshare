package info.note.app.feature.note.usecase

import info.note.app.feature.note.model.Note
import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.note.repository.toNote

class FetchNoteDetailsUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteId: String): Result<Note> =
        noteRepository.fetchNoteDetails(noteId).map { it.toNote() }
}