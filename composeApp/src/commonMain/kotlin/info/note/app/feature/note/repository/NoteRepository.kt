package info.note.app.feature.note.repository

import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    suspend fun addNote(note: NoteEntity): Result<Unit>
    suspend fun removeNote(noteId: String): Result<Unit>

    suspend fun fetchNoteDetails(noteId: String): Result<NoteEntity>

    fun fetchNotes(): Flow<List<NoteEntity>>
    suspend fun getAllNotes(): Result<List<NoteEntity>>

    suspend fun refreshNotes(noteList: List<NoteEntity>): Result<Unit>

    fun lastUpdateTime(): Long
}