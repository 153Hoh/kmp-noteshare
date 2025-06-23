package info.note.app.ui.settings.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.note.app.ui.settings.ConfirmationDialog
import info.note.app.ui.settings.Setting
import info.note.app.ui.settings.SyncStatus
import info.note.app.ui.settings.home.model.SettingsHomeEvents
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsHomeScreen(
    onNavigateToSyncShowQrClicked: () -> Unit,
    viewModel: SettingsHomeScreenViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            SyncStatus(status = state.value.syncStatus, lastSyncTime = state.value.lastSyncTime)
            Setting(title = "Sync with phone", onClick = onNavigateToSyncShowQrClicked)
            Setting(title = "Delete all notes", onClick = {
                viewModel.onEvent(
                    SettingsHomeEvents.ShowConfirmationDialog(
                        title = "Delete all notes",
                        message = "Are you sure you want to delete all notes? You cannot recover them!",
                        onConfirmClicked = {
                            viewModel.onEvent(SettingsHomeEvents.DeleteAllNotes)
                        }
                    )
                )
            })
        }

        if (state.value.confirmationDialogState.isShowing) {
            with(state.value.confirmationDialogState) {
                ConfirmationDialog(
                    modifier = Modifier.align(Alignment.Center),
                    title = title,
                    message = message,
                    onConfirmClicked = {
                        onConfirmClicked()
                        viewModel.onEvent(SettingsHomeEvents.HideConfirmationDialog)
                    },
                    onClose = { viewModel.onEvent(SettingsHomeEvents.HideConfirmationDialog) }
                )
            }
        }
    }
}