package info.note.app.feature.image.usecase

import info.note.app.feature.image.repository.ImagePickerCapabilities
import info.note.app.feature.image.repository.ImagePickerRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class IsCameraImageAvailableUseCaseTest:StringSpec({

    lateinit var sut: IsCameraImageAvailableUseCase

    val imagePickerRepository: ImagePickerRepository = mockk()

    beforeEach {
        sut = IsCameraImageAvailableUseCase(imagePickerRepository)
    }

    afterEach { unmockkAll() }

    "IsCameraImageAvailableUseCase returns true if the ImagePickerRepository has CAMERA capability" {
        runTest {
            every { imagePickerRepository.capabilities } returns listOf(ImagePickerCapabilities.CAMERA)

            sut() shouldBe true
        }
    }

    "IsCameraImageAvailableUseCase returns false if the ImagePickerRepository has CAMERA capability" {
        runTest {
            every { imagePickerRepository.capabilities } returns listOf(ImagePickerCapabilities.GALLERY)

            sut() shouldBe false
        }
    }
})