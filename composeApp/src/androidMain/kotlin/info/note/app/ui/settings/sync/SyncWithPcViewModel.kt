package info.note.app.ui.settings.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.domain.usecase.CheckServerUseCase
import info.note.app.domain.usecase.DisconnectSyncUseCase
import info.note.app.domain.usecase.FetchSyncKeyUseCase
import info.note.app.domain.usecase.SetSyncServerIpUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SyncWithPcViewModel(
    private val setSyncServerIpUseCase: SetSyncServerIpUseCase,
    private val checkServerUseCase: CheckServerUseCase,
    private val fetchSyncKeyUseCase: FetchSyncKeyUseCase,
    private val disconnectSyncUseCase: DisconnectSyncUseCase
) : ViewModel() {

    sealed class SyncWithPcEvent {
        data class QrResult(val qr: String?) : SyncWithPcEvent()
        data object DisconnectEvent : SyncWithPcEvent()
    }

    sealed class SyncWithPcEffect {
        data class ShowError(val message: String) : SyncWithPcEffect()
    }

    data class SyncWithPcState(
        val isScanning: Boolean = true,
        val connecting: Boolean = false,
        val connected: Boolean = false,
        val connectError: Boolean = false,
        val isAlreadySyncing: Boolean = false
    )

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

    private val _effect = Channel<SyncWithPcEffect>(Channel.CONFLATED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: SyncWithPcEvent) {
        viewModelScope.launch {
            when (event) {
                is SyncWithPcEvent.QrResult -> handleQr(event.qr)
                SyncWithPcEvent.DisconnectEvent -> {
                    disconnectSyncUseCase()
                    _effect.send(SyncWithPcEffect.ShowError("Disconnected successfully!"))
                    _state.update { it.copy(isScanning = true, isAlreadySyncing = false) }
                }
            }
        }
    }

    private suspend fun handleQr(qr: String?) {
        qr ?: return

        _state.update { it.copy(isScanning = false, connecting = true) }
        checkServerUseCase(qr).onSuccess {
            _state.update { it.copy(connecting = false, connected = true) }
            setSyncServerIpUseCase(qr)
        }.onFailure {
            _state.update { it.copy(connecting = false, connected = false, connectError = true) }
        }
    }

    private suspend fun checkSyncKey(): Boolean = fetchSyncKeyUseCase().isNotEmpty()
}