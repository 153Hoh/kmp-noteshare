package info.note.app.feature.note.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import info.note.app.feature.note.model.Note

@Entity
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val noteId: String = "",
    val title: String = "",
    val message: String = "",
    val creationTime: Long = 0L,
    val dueDate: Long = 0L,
    val isImportant: Boolean = false,
    val imageId: String = ""
)

fun NoteEntity.toNote() = Note(
    id = noteId,
    title = title,
    message = message,
    creationTime = creationTime,
    dueDate = dueDate,
    isImportant = isImportant,
    imageId = imageId
)