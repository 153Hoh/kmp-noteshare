package info.note.app.settings.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.usecase.FetchLastSyncStateUseCase
import info.note.app.usecase.FetchLastSyncTimeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsHomeScreenViewModel(
    private val fetchLastSyncStateUseCase: FetchLastSyncStateUseCase,
    private val fetchLastSyncTimeUseCase: FetchLastSyncTimeUseCase
) : ViewModel() {

    data class SettingsHomeState(
        val syncStatus: Boolean = false,
        val lastSyncTime: Long = 0L
    )

    private val _state = MutableStateFlow(SettingsHomeState())
    val state = _state.onStart {
        collectSyncState()
        collectSyncTime()
    }.stateIn(
        scope = viewModelScope,
        initialValue = SettingsHomeState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    private fun collectSyncState() {
        viewModelScope.launch {
            fetchLastSyncStateUseCase().collect { status ->
                _state.update { it.copy(syncStatus = status) }
            }
        }
    }

    private fun collectSyncTime() {
        viewModelScope.launch {
            fetchLastSyncTimeUseCase().collect { time ->
                _state.update { it.copy(lastSyncTime = time) }
            }
        }
    }
}