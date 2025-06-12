package info.note.app.db

import androidx.room.Room
import androidx.room.RoomDatabase
import info.note.app.feature.note.repository.DatabaseBuilder
import info.note.app.feature.note.repository.NoteDatabase
import java.io.File

class RoomDatabaseBuilder: DatabaseBuilder {

    override fun getDatabaseBuilder(): RoomDatabase.Builder<NoteDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), "my_room.db")
        return Room.databaseBuilder<NoteDatabase>(
            name = dbFile.absolutePath,
        )
    }
}