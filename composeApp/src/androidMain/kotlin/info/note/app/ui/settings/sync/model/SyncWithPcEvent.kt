package info.note.app.ui.settings.sync.model

sealed class SyncWithPcEvent {
    data class QrResult(val qr: String?) : SyncWithPcEvent()
    data object DisconnectEvent : SyncWithPcEvent()
}