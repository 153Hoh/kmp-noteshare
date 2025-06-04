package info.note.app.domain.repository.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(noteEntity: NoteEntity)

    @Insert
    suspend fun insertAllNotes(noteEntityList: List<NoteEntity>)

    @Query("SELECT * FROM NoteEntity WHERE noteId = :noteId")
    suspend fun getNoteDetailsById(noteId: String): NoteEntity

    @Query("SELECT * FROM NoteEntity")
    fun getAllNotesAsFlow() : Flow<List<NoteEntity>>

    @Query("SELECT * FROM NoteEntity")
    suspend fun getAllNotes(): List<NoteEntity>

    @Query("DELETE FROM NoteEntity WHERE noteId = :noteId")
    suspend fun deleteNote(noteId: String)

    @Query("DELETE FROM NoteEntity")
    suspend fun deleteAll()
}