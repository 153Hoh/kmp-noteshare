package info.note.app

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import info.note.app.di.coreModule
import info.note.app.domain.usecase.SetLastSyncStateUseCase
import info.note.app.server.SyncServerController
import info.note.app.ui.settings.SettingsScreen
import io.github.vinceglb.filekit.FileKit
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

fun main() = application {
    startKoin {
        modules(coreModule)
    }

    FileKit.init(appId = "NoteShare")

    val server: SyncServerController by inject(SyncServerController::class.java)

    server.start()

    LaunchedEffect(Unit) {
        val setLastSyncStateUseCase: SetLastSyncStateUseCase by inject(SetLastSyncStateUseCase::class.java)
        setLastSyncStateUseCase(false)
    }

    val state = rememberWindowState(
        width = 500.dp,
        height = 700.dp
    )

    Window(
        state = state,
        onCloseRequest = {
            server.stop()
            exitApplication()
        },
        title = "NoteShare",
    ) {
        App(settingsContent = { SettingsScreen() })
    }
}