package info.note.app.usecase

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import info.note.app.createNoteEntity
import info.note.app.domain.repository.note.NoteRepository
import info.note.app.domain.usecase.FetchNotesUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class FetchNotesUseCaseTest : StringSpec({

    val noteRepository: NoteRepository = mockk()

    lateinit var sut: FetchNotesUseCase

    beforeEach {

        every { noteRepository.fetchNotes() } returns flowOf(listOf(createNoteEntity(isImportant = false), createNoteEntity(isImportant = true)))

        sut = FetchNotesUseCase(noteRepository)
    }

    afterEach { unmockkAll() }

    "Returns valid flow of notes" {
        runTest {
            turbineScope {

                sut().test {
                    val item = awaitItem()
                    item.first().isImportant shouldBe true
                    item.last().isImportant shouldBe false
                    awaitComplete()
                }
            }
        }
    }

})