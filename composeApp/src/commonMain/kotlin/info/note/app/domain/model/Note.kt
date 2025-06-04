package info.note.app.domain.model

import info.note.app.domain.repository.db.NoteEntity
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String = "",
    val creationTime: Long = 0L,
    val title: String,
    val message: String,
    val dueDate: Long = 0L,
    val isImportant: Boolean = false
)

fun Note.toNoteEntity(): NoteEntity =
    NoteEntity(
        noteId = id,
        title = title,
        creationTime = creationTime,
        message = message,
        dueDate = dueDate,
        isImportant = isImportant
    )