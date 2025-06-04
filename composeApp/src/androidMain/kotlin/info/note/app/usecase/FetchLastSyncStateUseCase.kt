package info.note.app.usecase

import info.note.app.Preferences
import kotlinx.coroutines.flow.Flow

class FetchLastSyncStateUseCase(
    private val preferences: Preferences
) {

    operator fun invoke(): Flow<Boolean> = preferences.getLastSyncState()
}