package info.note.app.di

import info.note.app.JVMPlatform
import info.note.app.Platform
import info.note.app.db.RoomDatabaseBuilder
import info.note.app.feature.file.usecase.CreateCheckFileIdsResponseUseCase
import info.note.app.feature.file.usecase.FetchFileForDownloadUseCase
import info.note.app.feature.file.usecase.HandleFileUploadUseCase
import info.note.app.feature.image.repository.DesktopImagePickerRepository
import info.note.app.feature.image.repository.ImagePickerRepository
import info.note.app.feature.note.repository.DatabaseBuilder
import info.note.app.feature.note.usecase.FetchDeviceIpUseCase
import info.note.app.feature.note.usecase.SyncNotesUseCase
import info.note.app.feature.preferences.repository.AppPreferencesRepository
import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.feature.preferences.usecase.DisconnectSyncUseCase
import info.note.app.feature.preferences.usecase.FetchLastSyncStateUseCase
import info.note.app.feature.preferences.usecase.FetchLastSyncTimeUseCase
import info.note.app.feature.preferences.usecase.FetchSyncKeyUseCase
import info.note.app.feature.preferences.usecase.FetchThemeStateUseCase
import info.note.app.feature.preferences.usecase.HandleConnectUseCase
import info.note.app.feature.preferences.usecase.SetLastSyncStateUseCase
import info.note.app.feature.preferences.usecase.SetThemeStateUseCase
import info.note.app.feature.sync.SyncCoordinator
import info.note.app.server.SyncServerController
import info.note.app.server.routing.ServerRoutes
import info.note.app.server.websocket.WebSocketMessageHandler
import info.note.app.server.websocket.WebSocketMessageHandlerImpl
import info.note.app.server.websocket.usecase.FetchWebSocketMessagesToClientUseCase
import info.note.app.server.websocket.usecase.HandleWebSocketMessageUseCase
import info.note.app.ui.main.MainViewModel
import info.note.app.ui.settings.home.SettingsHomeScreenViewModel
import info.note.app.ui.settings.qr.ShowSyncQrViewModel
import info.note.app.ui.settings.screen.SettingsScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual fun platformModule() = module {
    single<DatabaseBuilder> { RoomDatabaseBuilder() }

    single<Platform> { JVMPlatform() }

    single<ImagePickerRepository> { DesktopImagePickerRepository() }

    single<PreferencesRepository> { AppPreferencesRepository() }

    single { SyncNotesUseCase(get(), get()) }
    single { SetLastSyncStateUseCase(get()) }
    single { FetchLastSyncStateUseCase(get()) }
    single { HandleConnectUseCase(get()) }
    single { FetchSyncKeyUseCase(get()) }
    single { FetchDeviceIpUseCase() }
    single { DisconnectSyncUseCase(get()) }
    single { FetchLastSyncTimeUseCase(get()) }
    single { CreateCheckFileIdsResponseUseCase(get()) }
    single { FetchFileForDownloadUseCase(get()) }
    single { HandleFileUploadUseCase(get()) }
    single { FetchThemeStateUseCase(get()) }
    single { SetThemeStateUseCase(get()) }
    single { FetchWebSocketMessagesToClientUseCase(get()) }
    single { HandleWebSocketMessageUseCase(get()) }

    single<ServerRoutes> {
        ServerRoutes(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<SyncServerController> { SyncServerController(get()) }
    single<WebSocketMessageHandler> { WebSocketMessageHandlerImpl() }

    single { SyncCoordinator(get(), get(), get()) }

    viewModel { ShowSyncQrViewModel(get(), get(), get()) }
    viewModel { SettingsScreenViewModel() }
    viewModel { SettingsHomeScreenViewModel(get(), get(), get()) }
    viewModel { MainViewModel(get(), get()) }
}