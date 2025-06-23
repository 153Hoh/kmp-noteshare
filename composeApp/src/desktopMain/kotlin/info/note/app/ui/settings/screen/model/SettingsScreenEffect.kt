package info.note.app.ui.settings.screen.model

sealed class SettingsScreenEffect {
    data class ShowSnackBar(val message: String) : SettingsScreenEffect()
}