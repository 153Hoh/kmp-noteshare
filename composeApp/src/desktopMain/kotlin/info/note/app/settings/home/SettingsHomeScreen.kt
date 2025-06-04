package info.note.app.settings.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.note.app.ui.settings.Setting
import info.note.app.ui.settings.SyncStatus
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsHomeScreen(
    onNavigateToSyncShowQrClicked: () -> Unit,
    viewModel: SettingsHomeScreenViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        SyncStatus(status = state.value.syncStatus, lastSyncTime = state.value.lastSyncTime)
        Setting(title = "Sync with phone", onClick = onNavigateToSyncShowQrClicked)
    }
}