package info.note.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteAppViewModel : ViewModel() {

    data class NoteAppState(
        val topBarTitle: String = "",
        val isOnHomeScreen: Boolean = false
    )

    sealed class NoteAppEffect {
        data class ShowSnackBar(val message: String) : NoteAppEffect()
    }

    sealed class NoteAppEvent {
        data class ShowSnackBar(val message: String) : NoteAppEvent()
        data class UpdateTopBar(val title: String, val isOnHomeScreen: Boolean) : NoteAppEvent()
    }

    private val _state = MutableStateFlow(NoteAppState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<NoteAppEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: NoteAppEvent) {
        viewModelScope.launch {
            when (event) {
                is NoteAppEvent.ShowSnackBar -> _effect.emit(NoteAppEffect.ShowSnackBar(event.message))
                is NoteAppEvent.UpdateTopBar -> _state.update {
                    it.copy(
                        topBarTitle = event.title,
                        isOnHomeScreen = event.isOnHomeScreen
                    )
                }
            }
        }
    }
}