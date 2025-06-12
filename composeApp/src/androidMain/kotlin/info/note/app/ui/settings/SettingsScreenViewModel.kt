package info.note.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsScreenViewModel : ViewModel() {

    sealed class SettingsScreenEffect {
        data class ShowSnackBar(val message: String) : SettingsScreenEffect()
    }

    sealed class SettingsScreenEvent {
        data class ShowSnackBar(val message: String) : SettingsScreenEvent()
    }

    private val _effect = MutableSharedFlow<SettingsScreenEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SettingsScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsScreenEvent.ShowSnackBar -> _effect.emit(
                    SettingsScreenEffect.ShowSnackBar(
                        event.message
                    )
                )
            }
        }
    }
}