package info.note.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import info.note.app.feature.sync.repository.NoteSyncController
import info.note.app.ui.settings.SettingsScreen
import info.note.app.ui.settings.permission.PermissionScreen
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val noteSyncHandler: NoteSyncController by inject()
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            noteSyncHandler.startSync()
        }

        FileKit.init(this)

        setContent {
            val state = viewModel.state.collectAsStateWithLifecycle()

            App(
                modifier = Modifier
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding(),
                settingsContent = {
                    SettingsScreen()
                },
                permissionScreen = { PermissionScreen(it) },
                onThemeStateChanged = {
                    viewModel.onEvent(
                        MainActivityViewModel.MainEvent.ThemeStateChanged(
                            it
                        )
                    )
                },
                themeState = state.value.themeState
            )
        }
    }

    override fun onDestroy() {
        noteSyncHandler.stopSync()
        super.onDestroy()
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckForPermission(
    permissions: List<String> = emptyList(),
    isGrantedContent: @Composable () -> Unit
) {
    val cameraPermissionState = rememberMultiplePermissionsState(
        permissions
    )

    if (cameraPermissionState.allPermissionsGranted) {
        isGrantedContent()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val textToShow = if (cameraPermissionState.shouldShowRationale) {
                "Without the camera or the Image access permission you cannot sync your notes to your PC. Please grant the permissions."
            } else {
                "Camera and Image access permission is required to use all the features of NoteShare, please grant the permissions!"
            }
            Text(text = textToShow, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchMultiplePermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}