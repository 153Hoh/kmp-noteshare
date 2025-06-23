package info.note.app.ui.settings.qr.model

sealed class ShowSyncQrEffect {
    data class ShowError(val message: String) : ShowSyncQrEffect()
}