package info.note.app.ui.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.domain.model.Note
import info.note.app.domain.usecase.FetchNotesUseCase
import info.note.app.domain.usecase.RemoveNoteUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteScreenViewModel(
    private val fetchNotesUseCase: FetchNotesUseCase,
    private val removeNoteUseCase: RemoveNoteUseCase
) : ViewModel() {

    data class NoteScreenState(
        val isLoading: Boolean = true,
        val noteList: List<Note> = emptyList()
    )

    sealed class NoteEffect {
        data class NavigateToNote(val noteId: String) : NoteEffect()
        data class ShowError(val message: String) : NoteEffect()
    }

    sealed class NoteScreenEvent {
        data class NoteClicked(val noteId: String) : NoteScreenEvent()
        data class RemoveNote(val noteId: String) : NoteScreenEvent()
    }

    private val _state = MutableStateFlow(NoteScreenState())
    val state = _state.onStart {
        viewModelScope.launch {
            fetchNotesUseCase().collect { result ->
                if (result.isEmpty()) {
                    _effect.send(NoteEffect.ShowError("There are no notes yet!"))
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

    private val _effect = Channel<NoteEffect>(capacity = Channel.CONFLATED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: NoteScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is NoteScreenEvent.NoteClicked -> _effect.send(NoteEffect.NavigateToNote(event.noteId))
                is NoteScreenEvent.RemoveNote -> removeNoteUseCase(event.noteId).onFailure {
                    _effect.send(
                        NoteEffect.ShowError("Cannot remove note!")
                    )
                }
            }
        }
    }
}