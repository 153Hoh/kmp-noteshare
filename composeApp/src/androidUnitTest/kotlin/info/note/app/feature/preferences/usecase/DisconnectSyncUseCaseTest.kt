package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository
import io.kotest.core.spec.style.StringSpec
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class DisconnectSyncUseCaseTest : StringSpec({

    lateinit var sut: DisconnectSyncUseCase

    val preferencesRepository: PreferencesRepository = mockk()

    beforeEach {
        sut = DisconnectSyncUseCase(preferencesRepository)

        coEvery { preferencesRepository.setLastSyncState(false) } returns Unit
        coEvery { preferencesRepository.setSyncKey("") } returns Unit
        coEvery { preferencesRepository.setSyncServerIp("") } returns Unit
        coEvery { preferencesRepository.setLastSyncTime(0L) } returns Unit
    }

    afterEach { unmockkAll() }

    "DisconnectSyncUseCase resets server preferences" {
        runTest {
            sut()

            coVerify { preferencesRepository.setLastSyncState(false) }
            coVerify { preferencesRepository.setLastSyncTime(0L) }
            coVerify { preferencesRepository.setSyncServerIp("") }
            coVerify { preferencesRepository.setSyncKey("") }
        }
    }
})