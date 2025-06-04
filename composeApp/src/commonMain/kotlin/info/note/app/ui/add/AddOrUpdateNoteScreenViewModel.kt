package info.note.app.ui.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import info.note.app.NoteScreens
import info.note.app.domain.usecase.AddOrUpdateNoteUseCase
import info.note.app.domain.usecase.FetchNoteDetailsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class AddOrUpdateNoteScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val addOrUpdateNoteUseCase: AddOrUpdateNoteUseCase,
    private val fetchNoteDetailsUseCase: FetchNoteDetailsUseCase
) : ViewModel() {

    data class AddNoteScreenState(
        val title: String = "",
        val message: String = "",
        val buttonTitle: String = "Add note",
        val hour: Int? = null,
        val minute: Int? = null,
        val dateInMillis: Long? = null,
        val isImportant: Boolean = false
    )

    sealed class AddNoteScreenEffect {
        data object NavigateBack : AddNoteScreenEffect()
        data class ShowError(val message: String) : AddNoteScreenEffect()
    }

    sealed class AddNoteScreenEvent {
        data class OnTitleUpdated(val title: String) : AddNoteScreenEvent()
        data class OnMessageUpdated(val message: String) : AddNoteScreenEvent()
        data class SetTimeEvent(val hour: Int, val minute: Int, val dateInMillis: Long) :
            AddNoteScreenEvent()

        data object ImportantClicked : AddNoteScreenEvent()
        data object AddNoteEvent : AddNoteScreenEvent()
    }

    private val params = savedStateHandle.toRoute<NoteScreens.AddOrUpdateNoteScreen>()

    private val _state = MutableStateFlow(AddNoteScreenState())
    val state = _state.onStart {
        viewModelScope.launch {
            val noteId = params.noteId

            if (!noteId.isNullOrEmpty()) {
                fetchNoteDetailsUseCase(noteId).onSuccess { note ->
                    val calendar = if (note.dueDate != 0L){
                        Calendar.getInstance().apply {
                            timeInMillis = note.dueDate
                        }
                    } else {
                        null
                    }
                    _state.update {
                        it.copy(
                            title = note.title,
                            message = note.message,
                            isImportant = note.isImportant,
                            dateInMillis = calendar?.timeInMillis,
                            hour = calendar?.get(Calendar.HOUR_OF_DAY),
                            minute = calendar?.get(Calendar.MINUTE),
                            buttonTitle = "Update note"
                        )
                    }
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = AddNoteScreenState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    private val _effect = Channel<AddNoteScreenEffect>(capacity = Channel.CONFLATED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: AddNoteScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is AddNoteScreenEvent.AddNoteEvent -> addNote()
                is AddNoteScreenEvent.OnMessageUpdated -> _state.update { it.copy(message = event.message) }
                is AddNoteScreenEvent.OnTitleUpdated -> _state.update { it.copy(title = event.title) }
                AddNoteScreenEvent.ImportantClicked -> setImportantState()
                is AddNoteScreenEvent.SetTimeEvent -> _state.update {
                    it.copy(
                        hour = event.hour,
                        minute = event.minute,
                        dateInMillis = event.dateInMillis
                    )
                }
            }
        }
    }

    private fun setImportantState() {
        _state.update { it.copy(isImportant = !state.value.isImportant) }
    }

    private suspend fun addNote() {
        if (state.value.title.isEmpty()) {
            _effect.send(AddNoteScreenEffect.ShowError("Cannot add a note without a title!"))
            return
        }

        with(state.value) {
            addOrUpdateNoteUseCase(
                noteId = params.noteId,
                title = title,
                message = message,
                isImportant = isImportant,
                hour = hour,
                minute = minute,
                dateInMillis = dateInMillis
            ).onSuccess { _effect.send(AddNoteScreenEffect.NavigateBack) }
                .onFailure { _effect.send(AddNoteScreenEffect.ShowError("Cannot add a note!")) }
        }
    }
}