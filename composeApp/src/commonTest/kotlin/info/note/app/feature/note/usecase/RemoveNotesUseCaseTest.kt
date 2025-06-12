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

class RemoveNotesUseCaseTest : StringSpec({

    val noteRepository: NoteRepository = mockk()
    val fileRepository: FileRepository = mockk()

    lateinit var sut: RemoveNoteUseCase

    beforeEach {

        coEvery { noteRepository.removeNote("noteId") } returns Result.success(Unit)
        coEvery {
            noteRepository.fetchNoteDetails("noteId")
        } returns Result.success(
            createNoteEntity(noteId = "noteId", imageId = "imageId")
        )
        coEvery { fileRepository.removeFileById("imageId") } returns Result.success(Unit)

        sut = RemoveNoteUseCase(noteRepository, fileRepository)
    }

    afterEach { unmockkAll() }

    "Remove note is called on repository" {
        runTest {
            sut("noteId")

            coVerify { noteRepository.removeNote("noteId") }
        }
    }

    "Remove file is called on repository" {
        runTest {
            sut("noteId")

            coVerify { fileRepository.removeFileById("imageId") }
        }
    }
})