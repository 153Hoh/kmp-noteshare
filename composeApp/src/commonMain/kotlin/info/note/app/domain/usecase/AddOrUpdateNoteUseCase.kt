package info.note.app.domain.usecase

import info.note.app.domain.repository.NoteRepository
import info.note.app.domain.repository.db.NoteEntity
import java.util.Calendar
import java.util.UUID

class AddOrUpdateNoteUseCase(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(
        noteId: String?,
        title: String,
        message: String,
        isImportant: Boolean,
        hour: Int?,
        minute: Int?,
        dateInMillis: Long?,
        creationTime: Long = System.currentTimeMillis()
    ): Result<Unit> {
        val id = noteId ?: UUID.randomUUID().toString()

        val dueDate = if (hour != null && minute != null && dateInMillis != null) {
            Calendar.getInstance().apply {
                timeInMillis = dateInMillis
                set(Calendar.MINUTE, minute)
                set(Calendar.HOUR_OF_DAY, hour)
            }.timeInMillis
        } else {
            0L
        }

        val note =
            NoteEntity(
                noteId = id,
                title = title,
                creationTime = creationTime,
                message = message,
                dueDate = dueDate,
                isImportant = isImportant
            )
        return noteRepository.addNote(note)
    }
}