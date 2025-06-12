package info.note.app.feature.note.model

import info.note.app.feature.note.repository.NoteEntity
import kotlinx.serialization.Serializable

@Serializable
data class Note(
    val id: String = "",
    val creationTime: Long = 0L,
    val title: String,
    val message: String,
    val dueDate: Long = 0L,
    val isImportant: Boolean = false,
    val imageId: String = ""
)

fun Note.toNoteEntity(): NoteEntity =
    NoteEntity(
        noteId = id,
        title = title,
        creationTime = creationTime,
        message = message,
        dueDate = dueDate,
        isImportant = isImportant,
        imageId = imageId
    )