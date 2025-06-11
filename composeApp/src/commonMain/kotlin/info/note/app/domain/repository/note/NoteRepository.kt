package info.note.app.domain.repository.note

import info.note.app.domain.repository.note.db.NoteEntity
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun addNote(note: NoteEntity): Result<Unit>
    suspend fun removeNote(noteId: String): Result<Unit>

    suspend fun fetchNoteDetails(noteId: String): Result<NoteEntity>

    fun fetchNotes(): Flow<List<NoteEntity>>
    suspend fun getAllNotes(): List<NoteEntity>

    suspend fun refreshNotes(noteList: List<NoteEntity>): Result<Unit>

    fun lastUpdateTime(): Long
}