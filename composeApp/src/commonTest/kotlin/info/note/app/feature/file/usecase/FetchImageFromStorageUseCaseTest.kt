package info.note.app.feature.file.usecase

import androidx.compose.ui.graphics.ImageBitmap
import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.image.model.ImageResult
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class FetchImageFromStorageUseCaseTest : StringSpec({

    lateinit var sut: FetchImageFromStorageUseCase

    val fileRepository: FileRepository = mockk()

    beforeEach {
        sut = FetchImageFromStorageUseCase(fileRepository)

        coEvery { fileRepository.loadImageFile("fileId") } returns Result.success(
            ImageResult(
                "",
                "",
                ImageBitmap(1, 1)
            )
        )
    }

    afterEach { unmockkAll() }

    "FetchImageFromStorageUseCase call fileRepository loadImageFile" {
        runTest {
            sut("fileId")

            coVerify { fileRepository.loadImageFile("fileId") }
        }
    }
})