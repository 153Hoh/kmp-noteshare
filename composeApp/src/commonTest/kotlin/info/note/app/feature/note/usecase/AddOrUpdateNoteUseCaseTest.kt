package info.note.app.feature.note.usecase

import androidx.compose.ui.graphics.ImageBitmap
import info.note.app.createNoteEntity
import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.image.model.ImageResult
import info.note.app.feature.note.repository.NoteRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import java.util.Calendar
import java.util.UUID

class AddOrUpdateNoteUseCaseTest : StringSpec({

    val noteRepository: NoteRepository = mockk()
    val fileRepository: FileRepository = mockk()

    lateinit var sut: AddOrUpdateNoteUseCase


    beforeEach {
        mockkStatic(UUID::class)

        every { UUID.randomUUID().toString() } returns "random"

        sut = AddOrUpdateNoteUseCase(noteRepository, fileRepository)

        coEvery { noteRepository.addNote(any()) } returns Result.success(Unit)
        coEvery { fileRepository.cacheImageFile(any(), any()) } returns Result.success("")
    }

    afterEach {
        unmockkAll()
        unmockkStatic(UUID::class)
    }

    "Creates valid noteEntity with given noteId" {
        runTest {
            val time = System.currentTimeMillis()
            val result = createNoteEntity(creationTime = time)
            sut(
                noteId = result.noteId,
                title = result.title,
                message = result.message,
                creationTime = result.creationTime,
                isImportant = false
            )

            coVerify {
                noteRepository.addNote(result)
            }
        }
    }

    "Creates valid noteEntity without noteId" {
        runTest {
            val time = System.currentTimeMillis()
            val result = createNoteEntity(noteId = "random", creationTime = time)
            sut(
                noteId = null,
                title = result.title,
                message = result.message,
                creationTime = result.creationTime,
                isImportant = false
            )

            coVerify {
                noteRepository.addNote(result)
            }
        }
    }

    "File is cached when image is not null" {
        runTest {
            val time = System.currentTimeMillis()
            val result = createNoteEntity(noteId = "random", creationTime = time)
            val imageResult = ImageResult(
                fileId = "Id",
                path = "path",
                bitmap = ImageBitmap(1,1)
            )
            sut(
                noteId = null,
                title = result.title,
                message = result.message,
                creationTime = result.creationTime,
                isImportant = false,
                image = imageResult
            )

            coVerify {
                fileRepository.cacheImageFile(imageResult.path, imageResult.fileId)
            }
        }
    }

    "DueDate is calculated correctly" {
        runTest {
            val time = System.currentTimeMillis()

            val resultTime = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.MINUTE, 10)
                set(Calendar.HOUR_OF_DAY, 10)
            }.timeInMillis

            val result = createNoteEntity(noteId = "random", creationTime = time, dueDate = resultTime)

            sut(
                noteId = null,
                title = result.title,
                message = result.message,
                creationTime = result.creationTime,
                isImportant = false,
                dateInMillis = System.currentTimeMillis(),
                hour = 10,
                minute = 10
            )

            coVerify {
                noteRepository.addNote(result)
            }
        }
    }
})