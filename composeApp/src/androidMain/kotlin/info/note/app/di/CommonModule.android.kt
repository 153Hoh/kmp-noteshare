package info.note.app.di

import info.note.app.AppPreferences
import info.note.app.Preferences
import info.note.app.db.RoomDatabaseBuilder
import info.note.app.domain.repository.NoteRepository
import info.note.app.domain.repository.db.DatabaseBuilder
import info.note.app.domain.repository.db.RoomNoteRepository
import info.note.app.settings.SettingsScreenViewModel
import info.note.app.usecase.FetchLastSyncStateUseCase
import info.note.app.usecase.FetchLastSyncTimeUseCase
import info.note.app.usecase.RemoveSyncIpUseCase
import info.note.app.settings.home.SettingsHomeScreenViewModel
import info.note.app.usecase.CheckServerUseCase
import info.note.app.usecase.DisconnectSyncUseCase
import info.note.app.usecase.FetchSyncKeyUseCase
import info.note.app.usecase.SetSyncServerIpUseCase
import info.note.app.settings.sync.SyncWithPcViewModel
import info.note.app.sync.INoteSyncHandler
import info.note.app.sync.KtorSyncRepository
import info.note.app.sync.NoteSyncHandler
import info.note.app.sync.SyncRepository
import info.note.app.usecase.GetAllNotesUseCase
import info.note.app.usecase.RefreshNotesUseCase
import info.note.app.usecase.SaveSyncStateUseCase
import info.note.app.usecase.ShouldSyncUseCase
import info.note.app.usecase.SyncNotesUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual fun platformModule() = module {
    single<DatabaseBuilder> { RoomDatabaseBuilder(androidContext()) }
    single<NoteRepository> { RoomNoteRepository(get()) }

    single<Preferences> { AppPreferences(androidContext()) }

    single<SyncRepository> { KtorSyncRepository(get()) }
    single<INoteSyncHandler> { NoteSyncHandler(get(), get(), get(), get(), get()) }

    single { GetAllNotesUseCase(get()) }
    single { RefreshNotesUseCase(get()) }
    single { SaveSyncStateUseCase(get()) }
    single { SyncNotesUseCase(get()) }
    single { ShouldSyncUseCase(get(), get(), get()) }
    single { FetchLastSyncStateUseCase(get()) }
    single { RemoveSyncIpUseCase(get()) }
    single { DisconnectSyncUseCase(get()) }
    single { FetchSyncKeyUseCase(get()) }
    single { FetchLastSyncTimeUseCase(get()) }

    single { CheckServerUseCase(get()) }
    single { SetSyncServerIpUseCase(get()) }

    viewModel { SettingsHomeScreenViewModel(get(), get()) }
    viewModel { SyncWithPcViewModel(get(), get(), get(), get()) }
    viewModel { SettingsScreenViewModel() }
}