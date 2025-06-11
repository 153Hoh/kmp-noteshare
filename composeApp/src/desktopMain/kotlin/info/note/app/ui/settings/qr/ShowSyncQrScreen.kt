package info.note.app.ui.settings.qr

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.note.app.rememberFlowWithLifecycle
import info.note.app.ui.settings.AlreadySyncingCard
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ShownSyncQrScreen(
    viewModel: ShowSyncQrViewModel = koinViewModel(),
    onShowSnackBar: (String) -> Unit,
    onNavigateBack: () -> Unit
) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    LaunchedEffect(effect) {
        effect.collect {
            when (it) {
                is ShowSyncQrViewModel.ShowSyncQrEffect.ShowError -> onShowSnackBar(it.message)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.value.isAlreadySyncing) {
            AlreadySyncingCard(
                onCancelClicked = onNavigateBack,
                onDisconnectClicked = { viewModel.onEvent(ShowSyncQrViewModel.ShowSyncQrEvent.DisconnectEvent) }
            )
        } else {
            Text(
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                text = "Scan this QR code with your phone in the Settings to start syncing your notes!"
            )
            if (!state.value.isLoading) {
                Image(
                    painter = rememberQrCodePainter(state.value.deviceIp) {
                        shapes {
                            frame = QrFrameShape.roundCorners(.25f)
                        }
                    },
                    contentDescription = ""
                )
            } else {
                Text("Loading QR code...")
            }
        }
    }
}