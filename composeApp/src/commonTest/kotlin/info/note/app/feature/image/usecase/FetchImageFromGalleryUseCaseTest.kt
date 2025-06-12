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

class FetchImageFromGalleryUseCaseTest : StringSpec({

    lateinit var sut: FetchImageFromGalleryUseCase

    val imagePickerRepository: ImagePickerRepository = mockk()

    beforeEach {
        sut = FetchImageFromGalleryUseCase(imagePickerRepository)

        coEvery { imagePickerRepository.fetchFromGallery() } returns Result.success(
            ImageResult(
                "",
                "",
                ImageBitmap(1, 1)
            )
        )
    }

    afterEach { unmockkAll() }

    "FetchImageFromGalleryUseCase calls imageRepository fetchFromGallery" {
        runTest {
            sut()

            coVerify { imagePickerRepository.fetchFromGallery() }
        }
    }
})