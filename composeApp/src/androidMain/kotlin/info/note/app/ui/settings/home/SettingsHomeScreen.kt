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
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsHomeScreen(
    onNavigateToSyncWithPCScreen: () -> Unit,
    onNavigateToPermissionScreen: () -> Unit,
    viewModel: SettingsHomeScreenViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            SyncStatus(status = state.value.syncStatus, lastSyncTime = state.value.lastSyncTime)
            Setting(title = "Sync with PC", onClick = onNavigateToSyncWithPCScreen)
            Setting(
                title = "Stop sync with PC",
                onClick = {
                    viewModel.onEvent(
                        SettingsHomeScreenViewModel.SettingsHomeEvents.ShowConfirmationDialog(
                            title = "Disable note sync",
                            message = "Are you sure you want to disable syncing?",
                            onConfirmClicked = {
                                viewModel.onEvent(SettingsHomeScreenViewModel.SettingsHomeEvents.DisableSyncEvent)
                            }
                        )
                    )
                }
            )
            Setting(title = "Grant permissions", onClick = onNavigateToPermissionScreen)
            Setting(title = "Delete all notes", onClick = {
                viewModel.onEvent(
                    SettingsHomeScreenViewModel.SettingsHomeEvents.ShowConfirmationDialog(
                        title = "Delete all notes",
                        message = "Are you sure you want to delete all notes? You cannot recover them!",
                        onConfirmClicked = {
                            viewModel.onEvent(SettingsHomeScreenViewModel.SettingsHomeEvents.DeleteAllNotes)
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
                        viewModel.onEvent(SettingsHomeScreenViewModel.SettingsHomeEvents.HideConfirmationDialog)
                    },
                    onClose = { viewModel.onEvent(SettingsHomeScreenViewModel.SettingsHomeEvents.HideConfirmationDialog) }
                )
            }
        }
    }
}