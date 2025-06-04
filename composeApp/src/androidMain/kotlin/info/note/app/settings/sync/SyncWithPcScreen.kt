package info.note.app.settings.sync

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import info.note.app.rememberFlowWithLifecycle
import info.note.app.ui.settings.AlreadySyncingCard
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel

@Composable
fun SyncWithPcScreen(
    viewModel: SyncWithPcViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onShowError: (String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    LaunchedEffect(effect) {
        effect.collect {
            when (it) {
                is SyncWithPcViewModel.SyncWithPcEffect.ShowError -> onShowError(it.message)
            }
        }
    }

    CheckForCameraPermission {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.value.isAlreadySyncing) {
                AlreadySyncingCard(
                    onCancelClicked = onNavigateBack,
                    onDisconnectClicked = { viewModel.onEvent(SyncWithPcViewModel.SyncWithPcEvent.DisconnectEvent) }
                )
            } else {
                if (state.value.isScanning) {
                    AndroidView(
                        factory = { context ->
                            val preview = CompoundBarcodeView(context)
                            preview.setStatusText("")
                            preview.cameraSettings.isAutoTorchEnabled = false
                            preview.apply {
                                val capture = CaptureManager(context as Activity, this)
                                capture.initializeFromIntent(context.intent, null)
                                capture.decode()
                                decodeContinuous { result ->
                                    if (state.value.isScanning) {
                                        viewModel.onEvent(
                                            SyncWithPcViewModel.SyncWithPcEvent.QrResult(
                                                result.text
                                            )
                                        )
                                    }
                                }
                                resume()
                            }
                        }
                    )
                }

                if (state.value.connecting) {
                    ConnectingCard()
                }

                if (state.value.connected) {
                    ConnectedCard(onClick = onNavigateBack)
                }
            }
        }
    }
}

@Composable
fun MessageCard(
    content: @Composable ColumnScope.() -> Unit = {}
) {
    Card(elevation = CardDefaults.elevatedCardElevation()) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Preview
@Composable
fun ConnectErrorCard(onClick: () -> Unit = {}) {
    MessageCard {
        Text(text = "Could not connect to the Server")
        TextButton(onClick) {
            Text("Return")
        }
    }
}

@Preview
@Composable
fun ConnectedCard(onClick: () -> Unit = {}) {
    MessageCard {
        Text(text = "Connected successfully to the server!")
        TextButton(onClick) {
            Text("Return")
        }
    }
}

@Preview
@Composable
fun ConnectingCard() {
    MessageCard {
        CircularProgressIndicator()
        Text(text = "Connecting to the Server...")
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckForCameraPermission(
    isGrantedContent: @Composable () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        isGrantedContent()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                "Without the camera you cannot sync your notes to your PC. Please grant the permission."
            } else {
                "Camera permission is required to Sync your notes with your PC, please grant the permission!"
            }
            Text(text = textToShow, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}
