package info.note.app.feature.sync.usecase

import info.note.app.feature.sync.repository.SyncRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class CheckServerUseCaseTest : StringSpec({

    lateinit var sut: CheckAndConnectToServerUseCase

    val syncRepository: SyncRepository = mockk()

    beforeEach {
        sut = CheckAndConnectToServerUseCase(syncRepository)

        coEvery { syncRepository.connectToServer("ip") } returns Result.success(Unit)
    }

    afterEach {
        unmockkAll()
    }

    "CheckServerUseCase tries to connect to server" {
        runTest {
            sut("ip")

            coVerify { syncRepository.connectToServer("ip") }
        }
    }
})