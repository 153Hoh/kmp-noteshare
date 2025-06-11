package info.note.app.usecase

import info.note.app.createNoteEntity
import info.note.app.domain.repository.file.FileRepository
import info.note.app.domain.repository.note.NoteRepository
import info.note.app.domain.usecase.AddOrUpdateNoteUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
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
})