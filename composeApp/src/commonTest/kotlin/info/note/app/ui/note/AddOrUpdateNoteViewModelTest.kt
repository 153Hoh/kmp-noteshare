package info.note.app.ui.note

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.testing.invoke
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import info.note.app.MainCoroutineListener
import info.note.app.NoteScreens
import info.note.app.createNote
import info.note.app.feature.file.usecase.FetchImageFromStorageUseCase
import info.note.app.feature.image.repository.exception.NoPermissionException
import info.note.app.feature.image.usecase.FetchImageFromCameraUseCase
import info.note.app.feature.image.usecase.FetchImageFromGalleryUseCase
import info.note.app.feature.image.usecase.IsCameraImageAvailableUseCase
import info.note.app.feature.image.usecase.IsGalleryImageAvailableUseCase
import info.note.app.feature.note.usecase.AddOrUpdateNoteUseCase
import info.note.app.feature.note.usecase.FetchNoteDetailsUseCase
import info.note.app.ui.add.NoteDetailsScreenViewModel
import info.note.app.ui.add.model.NoteDetailsEffect
import info.note.app.ui.add.model.NoteDetailsEvent
import info.note.app.ui.add.model.NoteState
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest


class AddOrUpdateNoteViewModelTest : StringSpec({

    val addOrUpdateNoteUseCase: AddOrUpdateNoteUseCase = mockk()
    val fetchNoteDetailsUseCase: FetchNoteDetailsUseCase = mockk()
    val isCameraImageAvailableUseCase: IsCameraImageAvailableUseCase = mockk()
    val isGalleryImageAvailableUseCase: IsGalleryImageAvailableUseCase = mockk()
    val fetchImageFromGalleryUseCase: FetchImageFromGalleryUseCase = mockk()
    val fetchImageFromCameraUseCase: FetchImageFromCameraUseCase = mockk()
    val fetchImageFromStorageUseCase: FetchImageFromStorageUseCase = mockk()

    lateinit var sut: NoteDetailsScreenViewModel

    fun createViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) {
        sut = NoteDetailsScreenViewModel(
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

    afterEach { unmockkAll() }

    // Fails on Android test runner
    "When noteId is given then noteDetails are fetched" {
        runTest {
            turbineScope {
                val resultNote = createNote(noteId = "noteId")

                createViewModel(
                    SavedStateHandle.Companion.invoke(
                        NoteScreens.NoteDetailsScreen(
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
                    item.noteState shouldBe NoteState.READ
                }
            }
        }
    }

    "When noteId is not given then noteDetails are not fetched" {
        runTest {
            turbineScope {

                createViewModel()

                sut.state.test {
                    awaitItem().noteState shouldBe NoteState.ADD
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
                    sut.onEvent(NoteDetailsEvent.OnTitleUpdated("title"))
                    skipItems(2)
                }
            }

            sut.onEvent(NoteDetailsEvent.AddNoteEvent)

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
                    sut.onEvent(NoteDetailsEvent.AddNoteEvent)

                    awaitItem() shouldBe NoteDetailsEffect.ShowError("Cannot add a note without a title!")

                    coVerify(exactly = 0) { addOrUpdateNoteUseCase(any(), any(), any(), any()) }
                }
            }
        }
    }

    "Add note navigates back after successful note save" {
        runTest {

            coEvery {
                addOrUpdateNoteUseCase(
                    noteId = any(),
                    title = "title",
                    message = any(),
                    isImportant = any(),
                    hour = any(),
                    minute = any(),
                    dateInMillis = any(),
                    image = any()
                )
            } returns Result.success(Unit)

            createViewModel()

            turbineScope {

                sut.state.test {
                    sut.onEvent(NoteDetailsEvent.OnTitleUpdated("title"))
                    skipItems(1)
                    awaitItem().title shouldBe "title"
                }

                sut.effect.test {
                    sut.onEvent(NoteDetailsEvent.AddNoteEvent)

                    awaitItem() shouldBe NoteDetailsEffect.NavigateBack
                }
            }
        }
    }

    "Add note fail shows error" {
        runTest {
            createViewModel()

            coEvery {
                addOrUpdateNoteUseCase(
                    noteId = null,
                    title = "titleFail",
                    message = "",
                    isImportant = false,
                    creationTime = any()
                )
            } returns Result.failure(Exception())

            turbineScope {

                sut.state.test {
                    sut.onEvent(NoteDetailsEvent.OnTitleUpdated("titleFail"))
                    skipItems(1)
                    awaitItem().title shouldBe "titleFail"
                }

                sut.effect.test {
                    sut.onEvent(NoteDetailsEvent.AddNoteEvent)

                    awaitItem() shouldBe NoteDetailsEffect.ShowError("Cannot add a note!")
                }
            }
        }
    }

    "OnTitleUpdatedEvent updates title in state" {
        runTest {
            createViewModel()

            turbineScope {
                sut.state.test {
                    sut.onEvent(NoteDetailsEvent.OnTitleUpdated("title"))
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
                    sut.onEvent(NoteDetailsEvent.OnMessageUpdated("message"))
                    skipItems(1)
                    awaitItem().message shouldBe "message"
                }
            }
        }
    }

    "AddImageFromCameraClickedEvent asks for permissions" {
        runTest {
            createViewModel()

            coEvery { fetchImageFromCameraUseCase() } returns Result.failure(NoPermissionException())

            turbineScope {
                sut.effect.test {
                    sut.onEvent(NoteDetailsEvent.AddImageFromCameraClicked)

                    awaitItem() shouldBe NoteDetailsEffect.PermissionRequired
                }
            }
        }
    }
}) {
    override fun listeners(): List<TestListener> = listOf(MainCoroutineListener())
}