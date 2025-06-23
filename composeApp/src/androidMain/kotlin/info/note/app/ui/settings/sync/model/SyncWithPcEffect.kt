package info.note.app.ui.settings.sync.model

sealed class SyncWithPcEffect {
    data class ShowError(val message: String) : SyncWithPcEffect()
}