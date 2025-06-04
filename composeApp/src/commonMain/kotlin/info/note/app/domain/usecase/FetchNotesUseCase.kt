package info.note.app.domain.usecase

import info.note.app.domain.model.Note
import info.note.app.domain.repository.NoteRepository
import info.note.app.domain.repository.db.toNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchNotesUseCase(
    private val noteRepository: NoteRepository
) {

    operator fun invoke(): Flow<List<Note>> = noteRepository.fetchNotes().map { entityList ->
        entityList.map { it.toNote() }.sortedByDescending { it.isImportant }
    }
}