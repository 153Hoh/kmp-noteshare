package info.note.app.ui.settings.sync.model

data class SyncWithPcState(
    val isScanning: Boolean = true,
    val connecting: Boolean = false,
    val connected: Boolean = false,
    val connectError: Boolean = false,
    val isAlreadySyncing: Boolean = false
)
