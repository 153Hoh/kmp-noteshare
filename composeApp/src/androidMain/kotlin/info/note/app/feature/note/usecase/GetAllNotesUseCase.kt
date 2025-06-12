package info.note.app.feature.note.usecase

import info.note.app.feature.note.model.Note
import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.note.repository.toNote

class GetAllNotesUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(): List<Note> =
        noteRepository.getAllNotes().map { it.toNote() }
}