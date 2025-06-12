package info.note.app.feature.image.usecase

import androidx.compose.ui.graphics.ImageBitmap
import info.note.app.feature.image.repository.ImagePickerRepository
import info.note.app.feature.image.model.ImageResult
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class FetchImageFromCameraUseCaseTest : StringSpec({

    lateinit var sut: FetchImageFromCameraUseCase

    val imagePickerRepository: ImagePickerRepository = mockk()

    beforeEach {
        sut = FetchImageFromCameraUseCase(imagePickerRepository)

        coEvery { imagePickerRepository.fetchFromCamera() } returns Result.success(
            ImageResult(
                "",
                "",
                ImageBitmap(1, 1)
            )
        )
    }

    afterEach {
        unmockkAll()
    }

    "FetchImageFromCameraUseCase calls imageRepository fetchFromCamera" {
        runTest {
            sut()

            coVerify { imagePickerRepository.fetchFromCamera() }
        }
    }
})