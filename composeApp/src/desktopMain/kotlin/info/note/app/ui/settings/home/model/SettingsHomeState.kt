package info.note.app.ui.settings.home.model

import info.note.app.ui.settings.ConfirmationDialogState

data class SettingsHomeState(
    val syncStatus: Boolean = false,
    val lastSyncTime: Long = 0L,
    val confirmationDialogState: ConfirmationDialogState = ConfirmationDialogState()
)