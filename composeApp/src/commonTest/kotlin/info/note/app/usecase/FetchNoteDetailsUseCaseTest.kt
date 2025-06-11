package info.note.app.usecase

import info.note.app.createNote
import info.note.app.createNoteEntity
import info.note.app.domain.repository.note.NoteRepository
import info.note.app.domain.usecase.FetchNoteDetailsUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class FetchNoteDetailsUseCaseTest : StringSpec({

    val noteRepository: NoteRepository = mockk()

    lateinit var sut: FetchNoteDetailsUseCase

    beforeEach {

        sut = FetchNoteDetailsUseCase(noteRepository)

        coEvery {
            noteRepository.fetchNoteDetails("noteId")
        } returns Result.success(
            createNoteEntity(noteId = "noteId")
        )
    }

    afterEach { unmockkAll() }

    "Fetches valid note" {
        runTest {
            val result = sut("noteId")

            result shouldBe Result.success(createNote(noteId = "noteId"))
        }
    }
})