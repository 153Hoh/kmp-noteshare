/*package info.note.app.usecase

import info.note.app.feature.sync.model.CheckFilesResponseBody
import info.note.app.domain.repository.file.FileRepository
import info.note.app.domain.usecase.CreateCheckFileIdsResponseUseCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class CreateCheckFileIdsResponseUseCaseTest : StringSpec({

    lateinit var sut: CreateCheckFileIdsResponseUseCase

    val fileRepository: FileRepository = mockk()

    beforeEach {

        coEvery { fileRepository.fetchImageFileIds() } returns Result.success(
            listOf(
                "1",
                "2",
                "3",
                "4"
            )
        )

        sut = CreateCheckFileIdsResponseUseCase(fileRepository)
    }


    "Creates valid response" {
        runTest {
            val response = sut(listOf("2", "5"))

            response shouldBe Result.success(
                CheckFilesResponseBody(
                    downloadList = listOf("1", "3", "4"),
                    uploadList = listOf("5")
                )
            )
        }
    }
})*/