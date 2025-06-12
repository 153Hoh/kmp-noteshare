package info.note.app.feature.note.repository

import androidx.room.RoomDatabase

interface DatabaseBuilder {

    fun getDatabaseBuilder(): RoomDatabase.Builder<NoteDatabase>
}