package info.note.app.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import info.note.app.MainCoroutineListener
import info.note.app.NoteScreens
import info.note.app.createNote
import info.note.app.domain.usecase.AddOrUpdateNoteUseCase
import info.note.app.domain.usecase.FetchImageFromCameraUseCase
import info.note.app.domain.usecase.FetchImageFromGalleryUseCase
import info.note.app.domain.usecase.FetchImageFromStorageUseCase
import info.note.app.domain.usecase.FetchNoteDetailsUseCase
import info.note.app.domain.usecase.IsCameraImageAvailableUseCase
import info.note.app.domain.usecase.IsGalleryImageAvailableUseCase
import info.note.app.ui.add.AddOrUpdateNoteScreenViewModel
import info.note.app.ui.add.AddOrUpdateNoteScreenViewModel.AddNoteScreenEffect
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest


class AddOrUpdateNoteViewModelTest : StringSpec({

    val addOrUpdateNoteUseCase: AddOrUpdateNoteUseCase = mockk()
    val fetchNoteDetailsUseCase: FetchNoteDetailsUseCase = mockk()
    val isCameraImageAvailableUseCase: IsCameraImageAvailableUseCase = mockk()
    val isGalleryImageAvailableUseCase: IsGalleryImageAvailableUseCase = mockk()
    val fetchImageFromGalleryUseCase: FetchImageFromGalleryUseCase = mockk()
    val fetchImageFromCameraUseCase: FetchImageFromCameraUseCase = mockk()
    val fetchImageFromStorageUseCase: FetchImageFromStorageUseCase = mockk()

    lateinit var sut: AddOrUpdateNoteScreenViewModel

    fun createViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) {
        sut = AddOrUpdateNoteScreenViewModel(
            savedStateHandle,
            addOrUpdateNoteUseCase,
            fetchNoteDetailsUseCase,
            isCameraImageAvailableUseCase,
            isGalleryImageAvailableUseCase,
            fetchImageFromGalleryUseCase,
            fetchImageFromCameraUseCase,
            fetchImageFromStorageUseCase
        )
    }

    // Fails on Android test runner
    "When noteId is given then noteDetails are fetched" {
        runTest {
            turbineScope {
                val resultNote = createNote(noteId = "noteId")

                createViewModel(
                    SavedStateHandle.Companion.invoke(
                        NoteScreens.AddOrUpdateNoteScreen(
                            resultNote.id
                        )
                    )
                )

                coEvery {
                    fetchNoteDetailsUseCase(resultNote.id)
                } returns Result.success(resultNote)

                sut.state.test {
                    val item = awaitItem()
                    item.title shouldBe resultNote.title
                    item.message shouldBe resultNote.message
                    item.buttonTitle shouldBe "Update note"
                }
            }
        }
    }

    "When noteId is not given then noteDetails are not fetched" {
        runTest {
            turbineScope {

                createViewModel()

                sut.state.test {
                    awaitItem().buttonTitle shouldBe "Add note"
                }
            }
        }
    }

    "Add note event calls addOrUpdateNoteUseCase" {
        runTest {
            createViewModel()

            coEvery {
                addOrUpdateNoteUseCase(
                    noteId = null,
                    title = "title",
                    message = "",
                    creationTime = any(),
                    isImportant = false,
                )
            } returns Result.success(Unit)

            turbineScope {
                sut.state.test {
                    sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.OnTitleUpdated("title"))
                    skipItems(2)
                }
            }

            sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.AddNoteEvent)

            coVerify {
                addOrUpdateNoteUseCase(
                    noteId = null,
                    title = "title",
                    message = "",
                    creationTime = any(),
                    isImportant = false
                )
            }
        }
    }

    "Add note fails without title" {
        runTest {
            turbineScope {
                createViewModel()

                sut.effect.test {
                    sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.AddNoteEvent)

                    awaitItem() shouldBe AddNoteScreenEffect.ShowError("Cannot add a note without a title!")

                    coVerify(exactly = 0) { addOrUpdateNoteUseCase(any(), any(), any(), any()) }
                }
            }
        }
    }

    "Add note navigates back after successful note save" {
        runTest {

            coEvery {
                addOrUpdateNoteUseCase(any(), any(), any(), any())
            } returns Result.success(Unit)

            createViewModel()

            turbineScope {

                sut.state.test {
                    sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.OnTitleUpdated("title"))
                    skipItems(1)
                    awaitItem().title shouldBe "title"
                }

                sut.effect.test {
                    sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.AddNoteEvent)

                    awaitItem() shouldBe AddNoteScreenEffect.NavigateBack
                }
            }
        }
    }

    "Add note fail shows error" {
        runTest {
            coEvery {
                addOrUpdateNoteUseCase(any(), any(), any(), any())
            } returns Result.failure(Exception())

            createViewModel()

            turbineScope {

                sut.state.test {
                    sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.OnTitleUpdated("title"))
                    skipItems(1)
                    awaitItem().title shouldBe "title"
                }

                sut.effect.test {
                    sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.AddNoteEvent)

                    awaitItem() shouldBe AddNoteScreenEffect.ShowError("Cannot add a note!")
                }
            }
        }
    }

    "OnTitleUpdatedEvent updates title in state" {
        runTest {
            createViewModel()

            turbineScope {
                sut.state.test {
                    sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.OnTitleUpdated("title"))
                    skipItems(1)
                    awaitItem().title shouldBe "title"
                }
            }
        }
    }

    "OnMessageUpdatedEvent updates message in state" {
        runTest {
            createViewModel()

            turbineScope {
                sut.state.test {
                    sut.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.OnMessageUpdated("message"))
                    skipItems(1)
                    awaitItem().message shouldBe "message"
                }
            }
        }
    }
}) {
    override fun listeners(): List<TestListener> = listOf(MainCoroutineListener())
}