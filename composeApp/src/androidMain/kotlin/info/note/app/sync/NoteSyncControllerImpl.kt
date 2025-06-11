package info.note.app.sync

import com.diamondedge.logging.logging
import info.note.app.domain.usecase.GetAllNotesUseCase
import info.note.app.domain.usecase.RefreshNotesUseCase
import info.note.app.domain.usecase.SaveSyncStateUseCase
import info.note.app.domain.usecase.ShouldSyncUseCase
import info.note.app.domain.usecase.SyncNotesUseCase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NoteSyncControllerImpl(
    private val syncNotesUseCase: SyncNotesUseCase,
    private val getAllNotesUseCase: GetAllNotesUseCase,
    private val refreshNotesUseCase: RefreshNotesUseCase,
    private val saveSyncStateUseCase: SaveSyncStateUseCase,
    private val shouldSyncUseCase: ShouldSyncUseCase
) : NoteSyncController {

    private var isRunning = true

    override suspend fun startSync() {
        saveSyncStateUseCase(false)
        coroutineScope {
            launch {
                while (isRunning) {
                    if (shouldSyncUseCase()) {
                        syncNotes()
                    }
                    delay(500L)
                }
            }
        }
    }

    override fun stopSync() {
        isRunning = false
    }

    private suspend fun syncNotes() {
        val noteList = getAllNotesUseCase()
        syncNotesUseCase(noteList).onSuccess {
            logging().info { "Notes synced to the server!" }
            refreshNotesUseCase(it)
            saveSyncStateUseCase(true)
        }.onFailure {
            logging().info { "Cannot sync notes to the server $it" }
            saveSyncStateUseCase(false)
        }
    }
}