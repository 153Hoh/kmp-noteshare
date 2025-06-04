package info.note.app.usecase

import info.note.app.domain.model.Note
import info.note.app.domain.repository.NoteRepository
import info.note.app.domain.repository.db.toNote

class GetAllNotesUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(): List<Note> =
        noteRepository.getAllNotes().map { it.toNote() }
}