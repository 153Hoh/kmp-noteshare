package info.note.app.ui

import app.cash.turbine.test
import app.cash.turbine.turbineScope
import info.note.app.NoteAppViewModel
import info.note.app.NoteAppViewModel.NoteAppEffect
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class NoteAppViewModelTest : StringSpec({

    lateinit var sut: NoteAppViewModel

    beforeEach {
        sut = NoteAppViewModel()
    }

    afterEach { unmockkAll() }

    "ShowSnackBar event shows snackBar" {
        runTest {
            turbineScope {
                sut.effect.test {
                    sut.onEvent(NoteAppViewModel.NoteAppEvent.ShowSnackBar("snack"))
                    awaitItem() shouldBe NoteAppEffect.ShowSnackBar("snack")
                }
            }
        }
    }

    "UpdateTopBar event updates state" {
        runTest {
            turbineScope {
                sut.state.test {
                    sut.onEvent(
                        NoteAppViewModel.NoteAppEvent.UpdateTopBar(
                            title = "title",
                            isOnHomeScreen = true
                        )
                    )
                    skipItems(1)
                    val item = awaitItem()
                    item.topBarTitle shouldBe "title"
                    item.isOnHomeScreen shouldBe true
                }
            }
        }
    }
})