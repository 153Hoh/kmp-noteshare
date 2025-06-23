package info.note.app.ui.settings.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.feature.note.usecase.DeleteAllNotesUseCase
import info.note.app.feature.preferences.usecase.FetchLastSyncStateUseCase
import info.note.app.feature.preferences.usecase.FetchLastSyncTimeUseCase
import info.note.app.ui.settings.ConfirmationDialogState
import info.note.app.ui.settings.home.model.SettingsHomeEvents
import info.note.app.ui.settings.home.model.SettingsHomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsHomeScreenViewModel(
    private val fetchLastSyncStateUseCase: FetchLastSyncStateUseCase,
    private val fetchLastSyncTimeUseCase: FetchLastSyncTimeUseCase,
    private val deleteAllNotesUseCase: DeleteAllNotesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsHomeState())
    val state = _state.onStart {
        collectSyncState()
        collectSyncTime()
    }.stateIn(
        scope = viewModelScope,
        initialValue = SettingsHomeState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    fun onEvent(event: SettingsHomeEvents) {
        viewModelScope.launch {
            when (event) {
                is SettingsHomeEvents.ShowConfirmationDialog -> _state.update {
                    it.copy(
                        confirmationDialogState = ConfirmationDialogState(
                            isShowing = true,
                            title = event.title,
                            message = event.message,
                            onConfirmClicked = event.onConfirmClicked
                        )
                    )
                }

                SettingsHomeEvents.HideConfirmationDialog -> _state.update {
                    it.copy(
                        confirmationDialogState = ConfirmationDialogState()
                    )
                }

                SettingsHomeEvents.DeleteAllNotes -> deleteAllNotesUseCase()
            }
        }
    }

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