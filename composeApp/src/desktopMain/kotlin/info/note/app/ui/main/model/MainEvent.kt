package info.note.app.ui.main.model

import info.note.app.ui.theme.ThemeState

sealed class MainEvent {
    data class ThemeStateChanged(val themeState: ThemeState) : MainEvent()
}