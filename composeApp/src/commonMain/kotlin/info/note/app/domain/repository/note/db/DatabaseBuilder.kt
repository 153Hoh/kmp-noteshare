package info.note.app.domain.repository.note.db

import androidx.room.RoomDatabase

interface DatabaseBuilder {

    fun getDatabaseBuilder(): RoomDatabase.Builder<NoteDatabase>
}