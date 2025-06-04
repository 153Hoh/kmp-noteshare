package info.note.app.settings.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.note.app.ui.settings.Setting
import info.note.app.ui.settings.SyncStatus
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsHomeScreen(
    onNavigateToSyncWithPCClicked: () -> Unit,
    viewModel: SettingsHomeScreenViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {

            SyncStatus(status = state.value.syncStatus, lastSyncTime = state.value.lastSyncTime)
            Setting(title = "Sync with PC", onClick = onNavigateToSyncWithPCClicked)
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
        }

        if (state.value.confirmationDialogState.isShowing) {
            with(state.value.confirmationDialogState) {
                ConfirmationDialog(
                    modifier = Modifier.weight(1f, false),
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

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onConfirmClicked: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    Card(modifier = modifier.padding(16.dp), elevation = CardDefaults.elevatedCardElevation()) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                text = title
            )
            HorizontalDivider()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = message,
                textAlign = TextAlign.Center
            )
            Row {
                TextButton(onClose) {
                    Text("Close")
                }
                TextButton(onConfirmClicked) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Preview
@Composable
fun ConfirmationDialogPreview() {
    MaterialTheme {
        ConfirmationDialog(
            title = "Disable note sync",
            message = "Are you sure you want to disable syncing?"
        )
    }
}