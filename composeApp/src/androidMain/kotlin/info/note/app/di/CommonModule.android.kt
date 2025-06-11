package info.note.app.di

import info.note.app.AndroidPlatform
import info.note.app.AppPreferences
import info.note.app.Platform
import info.note.app.Preferences
import info.note.app.db.RoomDatabaseBuilder
import info.note.app.domain.image.AndroidImagePickerRepository
import info.note.app.domain.repository.image.ImagePickerRepository
import info.note.app.domain.repository.note.db.DatabaseBuilder
import info.note.app.domain.repository.sync.KtorSyncRepository
import info.note.app.domain.repository.sync.SyncRepository
import info.note.app.domain.usecase.CheckServerUseCase
import info.note.app.domain.usecase.DisconnectSyncUseCase
import info.note.app.domain.usecase.FetchLastSyncStateUseCase
import info.note.app.domain.usecase.FetchLastSyncTimeUseCase
import info.note.app.domain.usecase.FetchSyncKeyUseCase
import info.note.app.domain.usecase.GetAllNotesUseCase
import info.note.app.domain.usecase.RefreshNotesUseCase
import info.note.app.domain.usecase.RemoveSyncIpUseCase
import info.note.app.domain.usecase.SaveSyncStateUseCase
import info.note.app.domain.usecase.SetSyncServerIpUseCase
import info.note.app.domain.usecase.ShouldSyncUseCase
import info.note.app.domain.usecase.SyncNotesUseCase
import info.note.app.sync.NoteSyncController
import info.note.app.sync.NoteSyncControllerImpl
import info.note.app.ui.settings.SettingsScreenViewModel
import info.note.app.ui.settings.home.SettingsHomeScreenViewModel
import info.note.app.ui.settings.sync.SyncWithPcViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual fun platformModule() = module {
    single<DatabaseBuilder> { RoomDatabaseBuilder(androidContext()) }

    single<ImagePickerRepository> { AndroidImagePickerRepository(androidContext()) }

    single<Preferences> { AppPreferences(androidContext()) }
    single<Platform> { AndroidPlatform(androidContext()) }

    single<SyncRepository> { KtorSyncRepository(get()) }
    single<NoteSyncController> { NoteSyncControllerImpl(get(), get(), get(), get(), get()) }

    single { GetAllNotesUseCase(get()) }
    single { RefreshNotesUseCase(get()) }
    single { SaveSyncStateUseCase(get()) }
    single { SyncNotesUseCase(get(), get(), get()) }
    single { ShouldSyncUseCase(get(), get(), get()) }
    single { FetchLastSyncStateUseCase(get()) }
    single { RemoveSyncIpUseCase(get()) }
    single { DisconnectSyncUseCase(get()) }
    single { FetchSyncKeyUseCase(get()) }
    single { FetchLastSyncTimeUseCase(get()) }

    single { CheckServerUseCase(get()) }
    single { SetSyncServerIpUseCase(get()) }

    viewModel { SettingsHomeScreenViewModel(get(), get(), get(), get()) }
    viewModel { SyncWithPcViewModel(get(), get(), get(), get()) }
    viewModel { SettingsScreenViewModel() }
}