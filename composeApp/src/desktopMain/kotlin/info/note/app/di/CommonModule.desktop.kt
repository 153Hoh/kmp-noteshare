package info.note.app.di

import info.note.app.AppPreferences
import info.note.app.JVMPlatform
import info.note.app.Platform
import info.note.app.Preferences
import info.note.app.db.RoomDatabaseBuilder
import info.note.app.domain.image.DesktopImagePickerRepository
import info.note.app.feature.image.repository.ImagePickerRepository
import info.note.app.feature.note.repository.DatabaseBuilder
import info.note.app.domain.usecase.CreateCheckFileIdsResponseUseCase
import info.note.app.domain.usecase.DisconnectSyncUseCase
import info.note.app.domain.usecase.FetchDeviceIpUseCase
import info.note.app.domain.usecase.FetchFileForDownloadUseCase
import info.note.app.domain.usecase.FetchLastSyncStateUseCase
import info.note.app.domain.usecase.FetchLastSyncTimeUseCase
import info.note.app.domain.usecase.FetchSyncKeyUseCase
import info.note.app.domain.usecase.HandleConnectUseCase
import info.note.app.domain.usecase.HandleFileUploadUseCase
import info.note.app.domain.usecase.SetLastSyncStateUseCase
import info.note.app.domain.usecase.ShouldSyncUseCase
import info.note.app.domain.usecase.SyncNotesUseCase
import info.note.app.server.SyncServerController
import info.note.app.server.routing.ServerRoutes
import info.note.app.ui.settings.SettingsScreenViewModel
import info.note.app.ui.settings.home.SettingsHomeScreenViewModel
import info.note.app.ui.settings.qr.ShowSyncQrViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual fun platformModule() = module {
    single<DatabaseBuilder> { RoomDatabaseBuilder() }

    single<Platform> { JVMPlatform() }

    single<ImagePickerRepository> { DesktopImagePickerRepository() }

    single<Preferences> { AppPreferences() }

    single { SyncNotesUseCase(get(), get()) }
    single { ShouldSyncUseCase(get(), get()) }
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

    single<ServerRoutes> { ServerRoutes(get(), get(), get(), get(), get(), get(), get(), get()) }
    single<SyncServerController> { SyncServerController(get()) }

    viewModel { ShowSyncQrViewModel(get(), get(), get()) }
    viewModel { SettingsScreenViewModel() }
    viewModel { SettingsHomeScreenViewModel(get(), get(), get()) }
}