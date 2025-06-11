package info.note.app.ui.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import info.note.app.NoteScreens
import info.note.app.domain.repository.image.ImageResult
import info.note.app.domain.repository.image.exception.CapabilityNotSupportedException
import info.note.app.domain.repository.image.exception.NoPermissionException
import info.note.app.domain.usecase.AddOrUpdateNoteUseCase
import info.note.app.domain.usecase.FetchImageFromCameraUseCase
import info.note.app.domain.usecase.FetchImageFromGalleryUseCase
import info.note.app.domain.usecase.FetchImageFromStorageUseCase
import info.note.app.domain.usecase.FetchNoteDetailsUseCase
import info.note.app.domain.usecase.IsCameraImageAvailableUseCase
import info.note.app.domain.usecase.IsGalleryImageAvailableUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class AddOrUpdateNoteScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val addOrUpdateNoteUseCase: AddOrUpdateNoteUseCase,
    private val fetchNoteDetailsUseCase: FetchNoteDetailsUseCase,
    private val isCameraImageAvailableUseCase: IsCameraImageAvailableUseCase,
    private val isGalleryImageAvailableUseCase: IsGalleryImageAvailableUseCase,
    private val fetchImageFromGalleryUseCase: FetchImageFromGalleryUseCase,
    private val fetchImageFromCameraUseCase: FetchImageFromCameraUseCase,
    private val fetchImageFromStorageUseCase: FetchImageFromStorageUseCase
) : ViewModel() {

    data class AddNoteScreenState(
        val isLoading: Boolean = true,
        val title: String = "",
        val message: String = "",
        val buttonTitle: String = "Add note",
        val hour: Int? = null,
        val minute: Int? = null,
        val dateInMillis: Long? = null,
        val isImportant: Boolean = false,
        val tempImage: ImageResult? = null,
        val image: ImageResult? = null,
        val highlightImage: Boolean = false
    )

    sealed class AddNoteScreenEffect {
        data object NavigateBack : AddNoteScreenEffect()
        data class ShowError(val message: String) : AddNoteScreenEffect()
        data object PermissionRequired : AddNoteScreenEffect()
    }

    sealed class AddNoteScreenEvent {
        data class OnTitleUpdated(val title: String) : AddNoteScreenEvent()
        data class OnMessageUpdated(val message: String) : AddNoteScreenEvent()
        data class SetTimeEvent(val hour: Int, val minute: Int, val dateInMillis: Long) :
            AddNoteScreenEvent()

        data object ImportantClicked : AddNoteScreenEvent()
        data object AddNoteEvent : AddNoteScreenEvent()
        data object AddImageFromGalleryClicked : AddNoteScreenEvent()
        data object AddImageFromCameraClicked : AddNoteScreenEvent()
        data object SetImageEvent : AddNoteScreenEvent()
        data object RemoveImage : AddNoteScreenEvent()
        data object ImageClicked : AddNoteScreenEvent()
        data object CloseImageHighlight : AddNoteScreenEvent()
    }

    private val params = savedStateHandle.toRoute<NoteScreens.AddOrUpdateNoteScreen>()

    private val _state = MutableStateFlow(AddNoteScreenState())
    val state = _state.onStart {
        viewModelScope.launch {
            val noteId = params.noteId

            if (!noteId.isNullOrEmpty()) {
                loadNoteFromId(noteId)
            }

            _state.update { it.copy(isLoading = false) }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = AddNoteScreenState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    private val _effect = Channel<AddNoteScreenEffect>(capacity = Channel.CONFLATED)
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: AddNoteScreenEvent) {
        viewModelScope.launch {
            when (event) {
                is AddNoteScreenEvent.AddNoteEvent -> addNote()
                is AddNoteScreenEvent.OnMessageUpdated -> _state.update { it.copy(message = event.message) }
                is AddNoteScreenEvent.OnTitleUpdated -> _state.update { it.copy(title = event.title) }
                AddNoteScreenEvent.ImportantClicked -> setImportantState()
                is AddNoteScreenEvent.SetTimeEvent -> _state.update {
                    it.copy(
                        hour = event.hour,
                        minute = event.minute,
                        dateInMillis = event.dateInMillis
                    )
                }

                AddNoteScreenEvent.AddImageFromCameraClicked -> handleImage(
                    fetchImageFromCameraUseCase()
                )

                AddNoteScreenEvent.AddImageFromGalleryClicked -> handleImage(
                    fetchImageFromGalleryUseCase()
                )

                AddNoteScreenEvent.SetImageEvent -> _state.update { it.copy(image = state.value.tempImage) }
                AddNoteScreenEvent.RemoveImage -> _state.update {
                    it.copy(
                        image = null,
                        tempImage = null
                    )
                }

                AddNoteScreenEvent.ImageClicked -> _state.update { it.copy(highlightImage = true) }
                AddNoteScreenEvent.CloseImageHighlight -> _state.update { it.copy(highlightImage = false) }
            }
        }
    }

    fun isCameraAvailable(): Boolean = isCameraImageAvailableUseCase()

    fun isGalleryAvailable(): Boolean = isGalleryImageAvailableUseCase()

    private fun setImportantState() {
        _state.update { it.copy(isImportant = !state.value.isImportant) }
    }

    private suspend fun addNote() {
        if (state.value.title.isEmpty()) {
            _effect.send(AddNoteScreenEffect.ShowError("Cannot add a note without a title!"))
            return
        }

        with(state.value) {
            addOrUpdateNoteUseCase(
                noteId = params.noteId,
                title = title,
                message = message,
                isImportant = isImportant,
                hour = hour,
                minute = minute,
                dateInMillis = dateInMillis,
                image = image
            ).onSuccess { _effect.send(AddNoteScreenEffect.NavigateBack) }
                .onFailure { _effect.send(AddNoteScreenEffect.ShowError("Cannot add a note!")) }
        }
    }

    private suspend fun handleImage(imageResult: Result<ImageResult>) {
        imageResult.onSuccess { result ->
            _state.update { it.copy(tempImage = result) }
        }.onFailure {
            when (it) {
                is NoPermissionException -> _effect.send(AddNoteScreenEffect.PermissionRequired)
                is CapabilityNotSupportedException -> _effect.send(AddNoteScreenEffect.ShowError("This device does not support this capability!"))
                else -> _effect.send(AddNoteScreenEffect.ShowError("Cannot load image!"))
            }
        }
    }

    private suspend fun loadNoteFromId(noteId: String) {
        fetchNoteDetailsUseCase(noteId).onSuccess { note ->
            val calendar = if (note.dueDate != 0L) {
                Calendar.getInstance().apply {
                    timeInMillis = note.dueDate
                }
            } else {
                null
            }

            val image = fetchImage(note.imageId)

            _state.update {
                it.copy(
                    title = note.title,
                    message = note.message,
                    isImportant = note.isImportant,
                    dateInMillis = calendar?.timeInMillis,
                    hour = calendar?.get(Calendar.HOUR_OF_DAY),
                    minute = calendar?.get(Calendar.MINUTE),
                    buttonTitle = "Update note",
                    image = image
                )
            }
        }
    }

    private suspend fun fetchImage(imageId: String): ImageResult? {
        if (imageId.isEmpty()) {
            return null
        }
        return fetchImageFromStorageUseCase(imageId).getOrNull()
    }
}