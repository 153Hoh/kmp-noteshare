package info.note.app.di

import info.note.app.NoteAppViewModel
import info.note.app.domain.repository.file.FileRepository
import info.note.app.domain.repository.file.FileRepositoryImpl
import info.note.app.domain.repository.note.NoteRepository
import info.note.app.domain.repository.note.db.RoomNoteRepository
import info.note.app.domain.usecase.AddOrUpdateNoteUseCase
import info.note.app.domain.usecase.DeleteAllNotesUseCase
import info.note.app.domain.usecase.FetchImageFromCameraUseCase
import info.note.app.domain.usecase.FetchImageFromGalleryUseCase
import info.note.app.domain.usecase.FetchImageFromStorageUseCase
import info.note.app.domain.usecase.FetchNoteDetailsUseCase
import info.note.app.domain.usecase.FetchNotesUseCase
import info.note.app.domain.usecase.IsCameraImageAvailableUseCase
import info.note.app.domain.usecase.IsGalleryImageAvailableUseCase
import info.note.app.domain.usecase.RemoveNoteUseCase
import info.note.app.ui.add.AddOrUpdateNoteScreenViewModel
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
    viewModel { AddOrUpdateNoteScreenViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { NoteAppViewModel() }
}