package info.note.app.ui.settings.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.ui.settings.screen.model.SettingsScreenEffect
import info.note.app.ui.settings.screen.model.SettingsScreenEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsScreenViewModel : ViewModel() {

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