package info.note.app.di

import info.note.app.AndroidPlatform
import info.note.app.feature.preferences.repository.AppPreferencesRepository
import info.note.app.Platform
import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.db.RoomDatabaseBuilder
import info.note.app.feature.image.repository.AndroidImagePickerRepository
import info.note.app.feature.image.repository.ImagePickerRepository
import info.note.app.feature.note.repository.DatabaseBuilder
import info.note.app.feature.sync.repository.KtorSyncRepository
import info.note.app.feature.sync.repository.SyncRepository
import info.note.app.feature.sync.usecase.CheckServerUseCase
import info.note.app.feature.preferences.usecase.DisconnectSyncUseCase
import info.note.app.feature.preferences.usecase.FetchLastSyncStateUseCase
import info.note.app.feature.preferences.usecase.FetchLastSyncTimeUseCase
import info.note.app.feature.preferences.usecase.FetchSyncKeyUseCase
import info.note.app.feature.note.usecase.GetAllNotesUseCase
import info.note.app.feature.note.usecase.RefreshNotesUseCase
import info.note.app.feature.preferences.usecase.RemoveSyncIpUseCase
import info.note.app.feature.preferences.usecase.SaveSyncStateUseCase
import info.note.app.feature.preferences.usecase.SetSyncServerIpUseCase
import info.note.app.feature.sync.usecase.ShouldSyncUseCase
import info.note.app.feature.sync.usecase.SyncNotesUseCase
import info.note.app.feature.sync.repository.NoteSyncController
import info.note.app.feature.sync.repository.NoteSyncControllerImpl
import info.note.app.ui.settings.SettingsScreenViewModel
import info.note.app.ui.settings.home.SettingsHomeScreenViewModel
import info.note.app.ui.settings.sync.SyncWithPcViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

actual fun platformModule() = module {
    single<DatabaseBuilder> { RoomDatabaseBuilder(androidContext()) }

    single<ImagePickerRepository> { AndroidImagePickerRepository(androidContext()) }

    single<PreferencesRepository> { AppPreferencesRepository(androidContext()) }
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