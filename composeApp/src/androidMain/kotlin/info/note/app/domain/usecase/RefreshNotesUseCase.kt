package info.note.app.domain.usecase

import info.note.app.domain.model.Note
import info.note.app.domain.model.toNoteEntity
import info.note.app.domain.repository.note.NoteRepository

class RefreshNotesUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteList: List<Note>): Result<Unit> =
        noteRepository.refreshNotes(
            noteList.map { it.toNoteEntity() }
        )
}