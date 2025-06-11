package info.note.app.domain.usecase

import info.note.app.domain.repository.sync.SyncRepository

class CheckServerUseCase(
    private val syncRepository: SyncRepository
) {

    suspend operator fun invoke(ip: String): Result<Unit> = syncRepository.connectToServer(ip)
}