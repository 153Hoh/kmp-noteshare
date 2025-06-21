package info.note.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.feature.preferences.usecase.FetchThemeStateUseCase
import info.note.app.feature.preferences.usecase.SetThemeStateUseCase
import info.note.app.ui.theme.ThemeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val fetchThemeStateUseCase: FetchThemeStateUseCase,
    private val setThemeStateUseCase: SetThemeStateUseCase
) : ViewModel() {

    data class MainState(
        val themeState: ThemeState = ThemeState.AUTO
    )

    sealed class MainEvent {
        data class ThemeStateChanged(val themeState: ThemeState) : MainEvent()
    }

    private val _state = MutableStateFlow(MainState())
    val state = _state.onStart {
        viewModelScope.launch {
            fetchThemeStateUseCase().collect { themeState ->
                _state.update { it.copy(themeState = themeState) }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    fun onEvent(event: MainEvent) {
        viewModelScope.launch {
            when (event) {
                is MainEvent.ThemeStateChanged -> setThemeStateUseCase(event.themeState)
            }
        }
    }
}