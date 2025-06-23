package info.note.app.ui.settings.qr.model

sealed class ShowSyncQrEvent {
    data object DisconnectEvent : ShowSyncQrEvent()
}