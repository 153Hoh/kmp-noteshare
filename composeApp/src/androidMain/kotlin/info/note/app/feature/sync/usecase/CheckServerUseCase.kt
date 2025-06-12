package info.note.app.feature.sync.usecase

import info.note.app.feature.sync.repository.SyncRepository

class CheckServerUseCase(
    private val syncRepository: SyncRepository
) {

    suspend operator fun invoke(ip: String): Result<Unit> = syncRepository.connectToServer(ip)
}