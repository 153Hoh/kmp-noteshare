package info.note.app.di

import info.note.app.AndroidPlatform
import info.note.app.Platform
import info.note.app.db.RoomDatabaseBuilder
import info.note.app.feature.image.repository.AndroidImagePickerRepository
import info.note.app.feature.image.repository.ImagePickerRepository
import info.note.app.feature.note.repository.DatabaseBuilder
import info.note.app.feature.note.usecase.GetAllNotesUseCase
import info.note.app.feature.note.usecase.RefreshNotesUseCase
import info.note.app.feature.preferences.repository.AppPreferencesRepository
import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.feature.preferences.usecase.DisconnectSyncUseCase
import info.note.app.feature.preferences.usecase.FetchLastSyncStateUseCase
import info.note.app.feature.preferences.usecase.FetchLastSyncTimeUseCase
import info.note.app.feature.preferences.usecase.FetchSyncIpUseCase
import info.note.app.feature.preferences.usecase.FetchSyncKeyUseCase
import info.note.app.feature.preferences.usecase.FetchThemeStateUseCase
import info.note.app.feature.preferences.usecase.RemoveSyncIpUseCase
import info.note.app.feature.preferences.usecase.SaveSyncStateUseCase
import info.note.app.feature.preferences.usecase.SetThemeStateUseCase
import info.note.app.feature.sync.repository.KtorSyncRepository
import info.note.app.feature.sync.repository.NoteSyncController
import info.note.app.feature.sync.repository.NoteSyncControllerImpl
import info.note.app.feature.sync.repository.SyncRepository
import info.note.app.feature.sync.repository.websocket.WebSocketController
import info.note.app.feature.sync.repository.websocket.WebSocketControllerImpl
import info.note.app.feature.sync.repository.websocket.WebSocketMessageHandler
import info.note.app.feature.sync.repository.websocket.WebSocketMessageHandlerImpl
import info.note.app.feature.sync.repository.websocket.usecase.FetchSyncWebSocketMessagesFromServerUseCase
import info.note.app.feature.sync.usecase.CheckAndConnectToServerUseCase
import info.note.app.feature.sync.usecase.ShouldSyncUseCase
import info.note.app.feature.sync.usecase.SyncNotesUseCase
import info.note.app.ui.activity.MainActivityViewModel
import info.note.app.ui.settings.home.SettingsHomeScreenViewModel
import info.note.app.ui.settings.screen.SettingsScreenViewModel
import info.note.app.ui.settings.sync.SyncWithPcViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual fun platformModule() = module {
    includes(androidUseCaseModule())
    single<DatabaseBuilder> { RoomDatabaseBuilder(androidContext()) }

    single<ImagePickerRepository> { AndroidImagePickerRepository(androidContext()) }

    single<PreferencesRepository> { AppPreferencesRepository(androidContext()) }
    single<Platform> { AndroidPlatform(androidContext()) }

    single<SyncRepository> { KtorSyncRepository() }
    single<NoteSyncController> {
        NoteSyncControllerImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<WebSocketMessageHandler> { WebSocketMessageHandlerImpl() }
    single<WebSocketController> { WebSocketControllerImpl(get()) }

    viewModel { SettingsHomeScreenViewModel(get(), get(), get(), get()) }
    viewModel { SyncWithPcViewModel(get(), get(), get()) }
    viewModel { SettingsScreenViewModel() }
    viewModel { MainActivityViewModel(get(), get()) }
}

fun androidUseCaseModule() = module {
    single { GetAllNotesUseCase(get()) }
    single { RefreshNotesUseCase(get()) }
    single { SaveSyncStateUseCase(get()) }
    single { SyncNotesUseCase(get(), get(), get()) }
    single { ShouldSyncUseCase(get(), get()) }
    single { FetchLastSyncStateUseCase(get()) }
    single { RemoveSyncIpUseCase(get()) }
    single { DisconnectSyncUseCase(get()) }
    single { FetchSyncKeyUseCase(get()) }
    single { FetchLastSyncTimeUseCase(get()) }
    single { FetchThemeStateUseCase(get()) }
    single { SetThemeStateUseCase(get()) }
    single { CheckAndConnectToServerUseCase(get(), get(), get()) }
    single { FetchSyncWebSocketMessagesFromServerUseCase(get()) }
    single { FetchSyncIpUseCase(get()) }
}