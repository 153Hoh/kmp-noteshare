package info.note.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingsScreenViewModel : ViewModel() {

    sealed class SettingsScreenEffect {
        data class ShowSnackBar(val message: String) : SettingsScreenEffect()
    }

    sealed class SettingsScreenEvent {
        data class ShowSnackBar(val message: String) : SettingsScreenEvent()
    }

    private val _effect = Channel<SettingsScreenEffect>(Channel.CONFLATED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: SettingsScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsScreenEvent.ShowSnackBar -> _effect.send(
                    SettingsScreenEffect.ShowSnackBar(
                        event.message
                    )
                )
            }
        }
    }
}