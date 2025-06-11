package info.note.app.domain.usecase

import info.note.app.domain.repository.file.FileRepository
import info.note.app.domain.repository.file.exception.FileSaveException
import info.note.app.domain.repository.image.ImageResult
import info.note.app.domain.repository.note.NoteRepository
import info.note.app.domain.repository.note.db.NoteEntity
import java.util.Calendar
import java.util.UUID

class AddOrUpdateNoteUseCase(
    private val noteRepository: NoteRepository,
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke(
        noteId: String?,
        title: String,
        message: String,
        isImportant: Boolean,
        hour: Int? = null,
        minute: Int? = null,
        dateInMillis: Long? = null,
        image: ImageResult? = null,
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

        if (image != null) {
            fileRepository.cacheImageFile(image.path, image.fileId)
                .onFailure { it.printStackTrace() }.getOrElse {
                return Result.failure(FileSaveException())
            }
        }

        val note =
            NoteEntity(
                noteId = id,
                title = title,
                creationTime = creationTime,
                message = message,
                dueDate = dueDate,
                isImportant = isImportant,
                imageId = image?.fileId ?: ""
            )
        return noteRepository.addNote(note).onFailure { it.printStackTrace() }
    }
}