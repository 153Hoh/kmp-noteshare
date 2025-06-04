package info.note.app.di

import info.note.app.NoteAppViewModel
import info.note.app.domain.usecase.AddOrUpdateNoteUseCase
import info.note.app.domain.usecase.FetchNoteDetailsUseCase
import info.note.app.domain.usecase.FetchNotesUseCase
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

    single { FetchNotesUseCase(get()) }
    single { AddOrUpdateNoteUseCase(get()) }
    single { RemoveNoteUseCase(get()) }
    single { FetchNoteDetailsUseCase(get()) }

    viewModel { NoteScreenViewModel(get(), get()) }
    viewModel { AddOrUpdateNoteScreenViewModel(get(), get(), get()) }
    viewModel { NoteAppViewModel() }
}