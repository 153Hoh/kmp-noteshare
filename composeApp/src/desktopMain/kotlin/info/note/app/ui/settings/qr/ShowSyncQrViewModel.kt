package info.note.app.ui.settings.qr

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import info.note.app.feature.note.usecase.FetchDeviceIpUseCase
import info.note.app.feature.preferences.usecase.DisconnectSyncUseCase
import info.note.app.feature.preferences.usecase.FetchSyncKeyUseCase
import info.note.app.ui.settings.qr.model.ShowSyncQrEffect
import info.note.app.ui.settings.qr.model.ShowSyncQrEvent
import info.note.app.ui.settings.qr.model.ShowSyncQrState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowSyncQrViewModel(
    private val fetchSyncKeyUseCase: FetchSyncKeyUseCase,
    private val fetchDeviceIpUseCase: FetchDeviceIpUseCase,
    private val disconnectSyncUseCase: DisconnectSyncUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShowSyncQrState())
    val state = _state.onStart {
        viewModelScope.launch {
            if (!checkSyncKey()) {
                fetchDeviceIp()
            } else {
                _state.update { it.copy(isLoading = false, isAlreadySyncing = true) }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = ShowSyncQrState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    private val _effect = MutableSharedFlow<ShowSyncQrEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: ShowSyncQrEvent) {
        viewModelScope.launch {
            when (event) {
                ShowSyncQrEvent.DisconnectEvent -> {
                    disconnectSyncUseCase()
                    _effect.emit(ShowSyncQrEffect.ShowError("Disconnected successfully!"))
                    _state.update { it.copy(isLoading = true, isAlreadySyncing = false) }
                    fetchDeviceIp()
                }
            }
        }
    }

    private suspend fun checkSyncKey(): Boolean = fetchSyncKeyUseCase().isNotEmpty()

    private suspend fun fetchDeviceIp() = withContext(Dispatchers.IO) {
        fetchDeviceIpUseCase().onSuccess { deviceIp ->
            _state.update { it.copy(isLoading = false, deviceIp = deviceIp) }
        }.onFailure {
            _effect.emit(ShowSyncQrEffect.ShowError("Cannot create QR code!"))
        }
    }
}