package info.note.app.settings.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.usecase.DisconnectSyncUseCase
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
    private val fetchLastSyncTimeUseCase: FetchLastSyncTimeUseCase,
    private val disconnectSyncUseCase: DisconnectSyncUseCase
) : ViewModel() {

    data class SettingsHomeState(
        val syncStatus: Boolean = false,
        val lastSyncTime: Long = 0L,
        val confirmationDialogState: ConfirmationDialogState = ConfirmationDialogState()
    )

    data class ConfirmationDialogState(
        val isShowing: Boolean = false,
        val title: String = "",
        val message: String = "",
        val onConfirmClicked: () -> Unit = {}
    )

    sealed class SettingsHomeEvents {
        data class ShowConfirmationDialog(
            val title: String,
            val message: String,
            val onConfirmClicked: () -> Unit
        ) : SettingsHomeEvents()

        data object HideConfirmationDialog : SettingsHomeEvents()

        data object DisableSyncEvent : SettingsHomeEvents()
    }

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

                SettingsHomeEvents.DisableSyncEvent -> disableSync()
                SettingsHomeEvents.HideConfirmationDialog -> _state.update {
                    it.copy(
                        confirmationDialogState = ConfirmationDialogState()
                    )
                }
            }
        }
    }

    private suspend fun disableSync() {
        disconnectSyncUseCase()
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