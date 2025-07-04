package info.note.app.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import info.note.app.feature.note.repository.DatabaseBuilder
import info.note.app.feature.note.repository.NoteDatabase

class RoomDatabaseBuilder(
    private val context: Context
): DatabaseBuilder {

    override fun getDatabaseBuilder(): RoomDatabase.Builder<NoteDatabase> {
        val dbFile = context.applicationContext.getDatabasePath("my_room.db")
        return Room.databaseBuilder<NoteDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
    }


}