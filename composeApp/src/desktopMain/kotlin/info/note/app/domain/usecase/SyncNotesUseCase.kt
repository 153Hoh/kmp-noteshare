package info.note.app.domain.usecase

import info.note.app.Preferences
import info.note.app.feature.note.model.Note
import info.note.app.feature.note.model.toNoteEntity
import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.note.repository.toNote
import kotlinx.coroutines.flow.first

class SyncNotesUseCase(
    private val noteRepository: NoteRepository,
    private val preferences: Preferences
) {

    suspend operator fun invoke(noteList: List<Note>): Result<List<Note>> = runCatching {
        val lastSyncTime = preferences.getLastSyncTime().first()

        val ownNotes = noteRepository
            .getAllNotes()
            .map { it.toNote() }

        val resultList = (ownNotes + noteList)
            .groupBy { it.id }
            .mapValues { (_, notes) ->
                if (notes.isEmpty()) {
                    null
                } else if (notes.size < 2) {
                    runCatching {
                        val note = notes.first()
                        if (note.creationTime > lastSyncTime) {
                            note
                        } else {
                            null
                        }
                    }.getOrNull()
                } else {
                    notes.maxByOrNull { it.creationTime }
                }
            }
            .values
            .toList()
            .filterNotNull()

        noteRepository.refreshNotes(
            resultList.map { it.toNoteEntity() }
        )

        preferences.setLastSyncTime(System.currentTimeMillis())

        return Result.success(resultList)
    }
}