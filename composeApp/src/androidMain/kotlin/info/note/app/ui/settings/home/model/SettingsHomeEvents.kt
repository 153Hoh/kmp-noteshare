package info.note.app.ui.settings.home.model

sealed class SettingsHomeEvents {
    data class ShowConfirmationDialog(
        val title: String,
        val message: String,
        val onConfirmClicked: () -> Unit
    ) : SettingsHomeEvents()

    data object HideConfirmationDialog : SettingsHomeEvents()

    data object DisableSyncEvent : SettingsHomeEvents()
    data object DeleteAllNotes : SettingsHomeEvents()
}
