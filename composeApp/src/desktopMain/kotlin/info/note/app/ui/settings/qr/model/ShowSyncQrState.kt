package info.note.app.ui.settings.qr.model

data class ShowSyncQrState(
    val isLoading: Boolean = true,
    val isAlreadySyncing: Boolean = false,
    val deviceIp: String = ""
)