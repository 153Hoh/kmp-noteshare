package info.note.app.di

import info.note.app.AppPreferences
import info.note.app.Preferences
import info.note.app.db.RoomDatabaseBuilder
import info.note.app.domain.repository.NoteRepository
import info.note.app.domain.repository.db.DatabaseBuilder
import info.note.app.domain.repository.db.RoomNoteRepository
import info.note.app.server.SyncServer
import info.note.app.server.routing.ServerRoutes
import info.note.app.settings.SettingsScreenViewModel
import info.note.app.settings.home.SettingsHomeScreenViewModel
import info.note.app.settings.qr.ShowSyncQrViewModel
import info.note.app.usecase.DisconnectSyncUseCase
import info.note.app.usecase.FetchDeviceIpUseCase
import info.note.app.usecase.FetchLastSyncStateUseCase
import info.note.app.usecase.FetchLastSyncTimeUseCase
import info.note.app.usecase.FetchSyncKeyUseCase
import info.note.app.usecase.HandleConnectUseCase
import info.note.app.usecase.SetLastSyncStateUseCase
import info.note.app.usecase.ShouldSyncUseCase
import info.note.app.usecase.SyncNotesUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual fun platformModule() = module {
    single<DatabaseBuilder> { RoomDatabaseBuilder() }
    single<NoteRepository> { RoomNoteRepository(get()) }

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

    single<ServerRoutes> { ServerRoutes(get(), get(), get(), get(), get()) }
    single<SyncServer> { SyncServer(get()) }

    viewModel { ShowSyncQrViewModel(get(), get(), get()) }
    viewModel { SettingsScreenViewModel() }
    viewModel { SettingsHomeScreenViewModel(get(), get()) }
}