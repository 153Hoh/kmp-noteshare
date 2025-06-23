package info.note.app.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.feature.note.usecase.FetchNotesUseCase
import info.note.app.feature.note.usecase.RemoveNoteUseCase
import info.note.app.ui.note.model.NoteEffect
import info.note.app.ui.note.model.NoteScreenEvent
import info.note.app.ui.note.model.NoteScreenState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteScreenViewModel(
    private val fetchNotesUseCase: FetchNotesUseCase,
    private val removeNoteUseCase: RemoveNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NoteScreenState())
    val state = _state.onStart {
        viewModelScope.launch {
            fetchNotesUseCase().collect { result ->
                if (result.isEmpty()) {
                    _state.update { it.copy(isLoading = false, noteList = emptyList()) }
                } else {
                    _state.update { it.copy(isLoading = false, noteList = result) }
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = NoteScreenState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    private val _effect = MutableSharedFlow<NoteEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: NoteScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is NoteScreenEvent.NoteClicked -> _effect.emit(NoteEffect.NavigateToNote(event.noteId))
                is NoteScreenEvent.RemoveNote -> removeNoteUseCase(event.noteId).onFailure {
                    _effect.emit(
                        NoteEffect.ShowError("Cannot remove note!")
                    )
                }
            }
        }
    }
}