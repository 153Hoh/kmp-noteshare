package info.note.app.domain.repository.db

import androidx.room.RoomDatabase

interface DatabaseBuilder {

    fun getDatabaseBuilder(): RoomDatabase.Builder<NoteDatabase>
}