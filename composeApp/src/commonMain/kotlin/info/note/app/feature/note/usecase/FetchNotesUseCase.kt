package info.note.app.feature.note.usecase

import info.note.app.feature.note.model.Note
import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.note.repository.toNote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchNotesUseCase(
    private val noteRepository: NoteRepository
) {

    operator fun invoke(): Flow<List<Note>> = noteRepository.fetchNotes().map { entityList ->
        entityList.map { it.toNote() }.sortedByDescending { it.isImportant }
    }
}