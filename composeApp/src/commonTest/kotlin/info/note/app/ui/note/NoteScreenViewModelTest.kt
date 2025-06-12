package info.note.app.ui.note

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import info.note.app.createNote
import info.note.app.feature.note.usecase.FetchNotesUseCase
import info.note.app.feature.note.usecase.RemoveNoteUseCase
import info.note.app.ui.note.NoteScreenViewModel.NoteEffect
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class NoteScreenViewModelTest : StringSpec({

    val fetchNotesUseCase: FetchNotesUseCase = mockk()
    val removeNotesUseCase: RemoveNoteUseCase = mockk()

    lateinit var sut: NoteScreenViewModel

    beforeEach {
        sut = NoteScreenViewModel(fetchNotesUseCase, removeNotesUseCase)
    }

    afterEach { unmockkAll() }

    "IsLoading is false when data is received" {
        runTest {
            turbineScope {
                every { fetchNotesUseCase() } returns flowOf(emptyList())

                sut.state.test {
                    skipItems(1)
                    val item = awaitItem()
                    item.isLoading shouldBe false
                }
            }
        }
    }

    "Valid NoteList is received when state is collected" {
        runTest {
            turbineScope {
                val noteList = listOf(createNote())
                every { fetchNotesUseCase() } returns flowOf(noteList)

                sut.state.test {
                    skipItems(1)
                    val item = awaitItem()
                    item.isLoading shouldBe false
                    item.noteList shouldBe noteList
                }
            }
        }
    }

    "No notes error is shown when noteList is empty" {
        runTest {
            turbineScope {
                every { fetchNotesUseCase() } returns flowOf(emptyList())
                sut.effect.test {
                    sut.state.test {
                        skipItems(2)
                    }

                    awaitItem() shouldBe NoteEffect.ShowError("There are no notes yet!")
                }
            }
        }
    }

    "RemoveNoteUseCase is called on RemoveNote event" {
        runTest {
            coEvery { removeNotesUseCase("noteId") } returns Result.success(Unit)

            sut.onEvent(NoteScreenViewModel.NoteScreenEvent.RemoveNote("noteId"))

            coVerify { removeNotesUseCase("noteId") }
        }
    }

    "Note remove error is shown when note cannot be removed" {
        runTest {
            turbineScope {
                coEvery { removeNotesUseCase("noteId") } returns Result.failure(Exception())

                sut.effect.test {
                    sut.onEvent(NoteScreenViewModel.NoteScreenEvent.RemoveNote("noteId"))

                    awaitItem() shouldBe NoteEffect.ShowError("Cannot remove note!")
                }
            }
        }
    }

    "Navigate to note is called on NoteClicked event" {
        runTest {
            turbineScope {
                sut.effect.test {
                    sut.onEvent(NoteScreenViewModel.NoteScreenEvent.NoteClicked("noteId"))

                    awaitItem() shouldBe NoteEffect.NavigateToNote("noteId")
                }
            }
        }
    }
})