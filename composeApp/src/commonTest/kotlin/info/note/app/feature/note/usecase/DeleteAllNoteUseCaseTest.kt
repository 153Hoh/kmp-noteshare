package info.note.app.feature.note.usecase

import info.note.app.createNoteEntity
import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.note.repository.NoteRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class DeleteAllNoteUseCaseTest : StringSpec({

    lateinit var sut: DeleteAllNotesUseCase

    val fileRepository: FileRepository = mockk()
    val noteRepository: NoteRepository = mockk()

    beforeEach {
        sut = DeleteAllNotesUseCase(noteRepository, fileRepository)

        coEvery { noteRepository.getAllNotes() } returns listOf(createNoteEntity())
        coEvery { noteRepository.removeNote(any()) } returns Result.success(Unit)
        coEvery { fileRepository.deleteAllFiles() } returns Result.success(Unit)
    }

    afterEach {
        unmockkAll()
    }

    "DeleteAllNotes removes every note" {
        runTest {
            sut()

            coVerify { noteRepository.removeNote(any()) }
        }
    }

    "DeleteAllNotes removed all images" {
        runTest {
            sut()

            coVerify { fileRepository.deleteAllFiles() }
        }
    }
})