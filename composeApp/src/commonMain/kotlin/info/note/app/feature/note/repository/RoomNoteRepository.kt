package info.note.app.feature.note.repository

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RoomNoteRepository(
    private val databaseBuilder: DatabaseBuilder
) : NoteRepository {

    private val dao by lazy {
        val database = databaseBuilder
            .getDatabaseBuilder()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

        database.getDao()
    }

    private var lastUpdateTime = System.currentTimeMillis()

    override suspend fun addNote(note: NoteEntity): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            fetchNoteDetails(note.noteId).onSuccess {
                dao.deleteNote(note.noteId)
            }
            dao.insertNote(note)
            lastUpdateTime = System.currentTimeMillis()
        }
    }

    override suspend fun removeNote(noteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            dao.deleteNote(noteId)
            lastUpdateTime = System.currentTimeMillis()
        }
    }

    override suspend fun fetchNoteDetails(noteId: String): Result<NoteEntity>  = withContext(Dispatchers.IO) {
        runCatching {
            dao.getNoteDetailsById(noteId)
        }
    }

    override fun fetchNotes(): Flow<List<NoteEntity>> =
        dao.getAllNotesAsFlow()

    override suspend fun getAllNotes(): List<NoteEntity> = dao.getAllNotes()

    override suspend fun refreshNotes(noteList: List<NoteEntity>): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                dao.deleteAll()
                dao.insertAllNotes(noteList)
            }
        }

    override fun lastUpdateTime(): Long = lastUpdateTime
}