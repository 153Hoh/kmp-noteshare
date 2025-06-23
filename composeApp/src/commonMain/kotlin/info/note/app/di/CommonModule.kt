package info.note.app.di

import info.note.app.NoteAppViewModel
import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.file.repository.FileRepositoryImpl
import info.note.app.feature.file.usecase.FetchImageFromStorageUseCase
import info.note.app.feature.image.usecase.FetchImageFromCameraUseCase
import info.note.app.feature.image.usecase.FetchImageFromGalleryUseCase
import info.note.app.feature.image.usecase.IsCameraImageAvailableUseCase
import info.note.app.feature.image.usecase.IsGalleryImageAvailableUseCase
import info.note.app.feature.note.repository.NoteRepository
import info.note.app.feature.note.repository.RoomNoteRepository
import info.note.app.feature.note.usecase.AddOrUpdateNoteUseCase
import info.note.app.feature.note.usecase.DeleteAllNotesUseCase
import info.note.app.feature.note.usecase.FetchNoteDetailsUseCase
import info.note.app.feature.note.usecase.FetchNotesUseCase
import info.note.app.feature.note.usecase.RemoveNoteUseCase
import info.note.app.ui.add.NoteDetailsScreenViewModel
import info.note.app.ui.note.NoteScreenViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun platformModule(): Module

val coreModule: Module
    get() = module {
        includes(commonModule() + platformModule())
    }

fun commonModule() = module {

    single<NoteRepository> { RoomNoteRepository(get()) }
    single<FileRepository> { FileRepositoryImpl(get()) }

    single { FetchNotesUseCase(get()) }
    single { AddOrUpdateNoteUseCase(get(), get()) }
    single { RemoveNoteUseCase(get(), get()) }
    single { FetchNoteDetailsUseCase(get()) }
    single { FetchImageFromStorageUseCase(get()) }

    single { DeleteAllNotesUseCase(get(), get()) }

    single { IsCameraImageAvailableUseCase(get()) }
    single { IsGalleryImageAvailableUseCase(get()) }
    single { FetchImageFromCameraUseCase(get()) }
    single { FetchImageFromGalleryUseCase(get()) }

    viewModel { NoteScreenViewModel(get(), get()) }
    viewModel { NoteDetailsScreenViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { NoteAppViewModel() }
}