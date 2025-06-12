package info.note.app.feature.note.usecase

import info.note.app.feature.note.model.Note
import info.note.app.feature.note.model.toNoteEntity
import info.note.app.feature.note.repository.NoteRepository

class RefreshNotesUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(noteList: List<Note>): Result<Unit> =
        noteRepository.refreshNotes(
            noteList.map { it.toNoteEntity() }
        )
}