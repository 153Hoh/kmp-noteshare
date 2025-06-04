package info.note.app.usecase

import info.note.app.sync.SyncRepository

class CheckServerUseCase(
    private val syncRepository: SyncRepository
) {

    suspend operator fun invoke(ip: String): Result<Unit> = syncRepository.connectToServer(ip)
}