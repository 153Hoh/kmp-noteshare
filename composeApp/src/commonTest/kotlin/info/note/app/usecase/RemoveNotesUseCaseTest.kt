package info.note.app.usecase

import info.note.app.domain.repository.NoteRepository
import info.note.app.domain.usecase.RemoveNoteUseCase
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class RemoveNotesUseCaseTest : StringSpec({

    val noteRepository: NoteRepository = mockk()

    lateinit var sut: RemoveNoteUseCase

    beforeEach {

        coEvery { noteRepository.removeNote("noteId") } returns Result.success(Unit)

        sut = RemoveNoteUseCase(noteRepository)
    }

    afterEach { unmockkAll() }

    "Remove note is called on repository" {
        runTest {
            sut("noteId")

            coVerify { noteRepository.removeNote("noteId") }
        }
    }
})