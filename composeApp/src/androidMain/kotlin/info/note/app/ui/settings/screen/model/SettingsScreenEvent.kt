package info.note.app.ui.settings.screen.model

sealed class SettingsScreenEvent {
    data class ShowSnackBar(val message: String) : SettingsScreenEvent()
}