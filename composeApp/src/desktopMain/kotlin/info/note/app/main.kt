package info.note.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import info.note.app.di.coreModule
import info.note.app.feature.preferences.usecase.SetLastSyncStateUseCase
import info.note.app.server.SyncServerController
import info.note.app.ui.main.MainViewModel
import info.note.app.ui.main.model.MainEvent
import info.note.app.ui.settings.screen.SettingsScreen
import io.github.vinceglb.filekit.FileKit
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

fun main() = application {
    startKoin {
        modules(coreModule)
    }

    FileKit.init(appId = "NoteShare")

    val server: SyncServerController by inject(SyncServerController::class.java)
    val viewModel: MainViewModel by inject(MainViewModel::class.java)

    server.start()

    LaunchedEffect(Unit) {
        val setLastSyncStateUseCase: SetLastSyncStateUseCase by inject(SetLastSyncStateUseCase::class.java)
        setLastSyncStateUseCase(false)
    }

    val windowState = rememberWindowState(
        width = 500.dp,
        height = 700.dp
    )

    val state = viewModel.state.collectAsState()

    Window(
        state = windowState,
        onCloseRequest = {
            server.stop()
            exitApplication()
        },
        title = "NoteShare",
        icon = painterResource("ic_launcher.png")
    ) {
        App(
            settingsContent = { SettingsScreen() },
            onThemeStateChanged = {
                viewModel.onEvent(
                    MainEvent.ThemeStateChanged(
                        it
                    )
                )
            },
            themeState = state.value.themeState
        )
    }
}