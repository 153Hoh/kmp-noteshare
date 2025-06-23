package info.note.app.ui.settings.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.feature.preferences.usecase.DisconnectSyncUseCase
import info.note.app.feature.preferences.usecase.FetchSyncKeyUseCase
import info.note.app.feature.sync.usecase.CheckAndConnectToServerUseCase
import info.note.app.ui.settings.sync.model.SyncWithPcEffect
import info.note.app.ui.settings.sync.model.SyncWithPcEvent
import info.note.app.ui.settings.sync.model.SyncWithPcState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SyncWithPcViewModel(
    private val checkAndConnectToServerUseCase: CheckAndConnectToServerUseCase,
    private val fetchSyncKeyUseCase: FetchSyncKeyUseCase,
    private val disconnectSyncUseCase: DisconnectSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SyncWithPcState())
    val state = _state.onStart {
        if (checkSyncKey()) {
            _state.update { it.copy(isScanning = false, isAlreadySyncing = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = SyncWithPcState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    private val _effect = MutableSharedFlow<SyncWithPcEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SyncWithPcEvent) {
        viewModelScope.launch {
            when (event) {
                is SyncWithPcEvent.QrResult -> handleQr(event.qr)
                SyncWithPcEvent.DisconnectEvent -> {
                    disconnectSyncUseCase()
                    _effect.emit(SyncWithPcEffect.ShowError("Disconnected successfully!"))
                    _state.update { it.copy(isScanning = true, isAlreadySyncing = false) }
                }
            }
        }
    }

    private suspend fun handleQr(qr: String?) {
        qr ?: return

        _state.update { it.copy(isScanning = false, connecting = true) }
        checkAndConnectToServerUseCase(qr).onSuccess {
            _state.update { it.copy(connecting = false, connected = true) }
        }.onFailure {
            _state.update { it.copy(connecting = false, connected = false, connectError = true) }
        }
    }

    private suspend fun checkSyncKey(): Boolean = fetchSyncKeyUseCase().isNotEmpty()
}