package info.note.app.feature.image.usecase

import info.note.app.feature.image.repository.ImagePickerCapabilities
import info.note.app.feature.image.repository.ImagePickerRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class IsGalleryImageAvailableUseCaseTest : StringSpec({

    lateinit var sut: IsGalleryImageAvailableUseCase

    val imagePickerRepository: ImagePickerRepository = mockk()

    beforeEach {
        sut = IsGalleryImageAvailableUseCase(imagePickerRepository)
    }

    afterEach { unmockkAll() }

    "IsGalleryImageAvailableUseCase returns true if the ImagePickerRepository has GALLERY capability" {
        runTest {
            every { imagePickerRepository.capabilities } returns listOf(ImagePickerCapabilities.GALLERY)

            sut() shouldBe true
        }
    }

    "IsGalleryImageAvailableUseCase returns false if the ImagePickerRepository has CAMERA capability" {
        runTest {
            every { imagePickerRepository.capabilities } returns listOf(ImagePickerCapabilities.CAMERA)

            sut() shouldBe false
        }
    }
})