package info.note.app.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import info.note.app.NoteScreens
import info.note.app.feature.file.usecase.FetchImageFromStorageUseCase
import info.note.app.feature.image.model.ImageResult
import info.note.app.feature.image.repository.exception.CapabilityNotSupportedException
import info.note.app.feature.image.repository.exception.NoPermissionException
import info.note.app.feature.image.usecase.FetchImageFromCameraUseCase
import info.note.app.feature.image.usecase.FetchImageFromGalleryUseCase
import info.note.app.feature.image.usecase.IsCameraImageAvailableUseCase
import info.note.app.feature.image.usecase.IsGalleryImageAvailableUseCase
import info.note.app.feature.note.usecase.AddOrUpdateNoteUseCase
import info.note.app.feature.note.usecase.FetchNoteDetailsUseCase
import info.note.app.ui.details.model.NoteDetailsEffect
import info.note.app.ui.details.model.NoteDetailsEvent
import info.note.app.ui.details.model.NoteDetailsState
import info.note.app.ui.details.model.NoteState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class NoteDetailsScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val addOrUpdateNoteUseCase: AddOrUpdateNoteUseCase,
    private val fetchNoteDetailsUseCase: FetchNoteDetailsUseCase,
    private val isCameraImageAvailableUseCase: IsCameraImageAvailableUseCase,
    private val isGalleryImageAvailableUseCase: IsGalleryImageAvailableUseCase,
    private val fetchImageFromGalleryUseCase: FetchImageFromGalleryUseCase,
    private val fetchImageFromCameraUseCase: FetchImageFromCameraUseCase,
    private val fetchImageFromStorageUseCase: FetchImageFromStorageUseCase
) : ViewModel() {

    private val params = savedStateHandle.toRoute<NoteScreens.NoteDetailsScreen>()

    private val _state = MutableStateFlow(NoteDetailsState())
    val state = _state.onStart {
        viewModelScope.launch {
            val noteId = params.noteId

            if (!noteId.isNullOrEmpty()) {
                loadNoteFromId(noteId)
            } else {
                updateNoteState(NoteState.ADD)
            }

            _state.update { it.copy(isLoading = false) }
        }
    }.stateIn(
        scope = viewModelScope,
        initialValue = NoteDetailsState(),
        started = SharingStarted.WhileSubscribed(5000L)
    )

    private val _effect = MutableSharedFlow<NoteDetailsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: NoteDetailsEvent) {
        viewModelScope.launch {
            when (event) {
                is NoteDetailsEvent.AddNoteEvent -> addNote()
                is NoteDetailsEvent.OnMessageUpdated -> _state.update { it.copy(message = event.message) }
                is NoteDetailsEvent.OnTitleUpdated -> _state.update { it.copy(title = event.title) }
                NoteDetailsEvent.ImportantClicked -> setImportantState()
                is NoteDetailsEvent.SetTimeEvent -> _state.update {
                    it.copy(
                        hour = event.hour,
                        minute = event.minute,
                        dateInMillis = event.dateInMillis
                    )
                }

                NoteDetailsEvent.AddImageFromCameraClicked -> handleImage(
                    fetchImageFromCameraUseCase()
                )

                NoteDetailsEvent.AddImageFromGalleryClicked -> handleImage(
                    fetchImageFromGalleryUseCase()
                )

                NoteDetailsEvent.SetImageEvent -> _state.update { it.copy(image = state.value.tempImage) }
                NoteDetailsEvent.RemoveImage -> _state.update {
                    it.copy(
                        image = null,
                        tempImage = null
                    )
                }

                NoteDetailsEvent.ImageClicked -> _state.update { it.copy(highlightImage = true) }
                NoteDetailsEvent.CloseImageHighlight -> _state.update { it.copy(highlightImage = false) }
                NoteDetailsEvent.EditClicked -> updateNoteState(NoteState.EDIT)
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
            _effect.emit(NoteDetailsEffect.ShowError("Cannot add a note without a title!"))
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
            ).onSuccess {
                _effect.emit(NoteDetailsEffect.NavigateBack)
            }.onFailure {
                it.printStackTrace()
                _effect.emit(NoteDetailsEffect.ShowError("Cannot add a note!"))
            }
        }
    }

    private suspend fun handleImage(imageResult: Result<ImageResult>) {
        imageResult.onSuccess { result ->
            _state.update { it.copy(tempImage = result) }
        }.onFailure {
            when (it) {
                is NoPermissionException -> _effect.emit(NoteDetailsEffect.PermissionRequired)
                is CapabilityNotSupportedException -> _effect.emit(NoteDetailsEffect.ShowError("This device does not support this capability!"))
                else -> _effect.emit(NoteDetailsEffect.ShowError("Cannot load image!"))
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
                    image = image,
                )
            }
            updateNoteState(NoteState.READ)
        }
    }

    private suspend fun fetchImage(imageId: String): ImageResult? {
        if (imageId.isEmpty()) {
            return null
        }
        return fetchImageFromStorageUseCase(imageId).getOrNull()
    }

    private suspend fun updateNoteState(noteState: NoteState) {
        _state.update { it.copy(noteState = noteState) }
        val newNoteTitle = when (noteState) {
            NoteState.ADD -> "Add note"
            NoteState.READ -> "Read note"
            NoteState.EDIT -> "Edit note"
        }
        _effect.emit(NoteDetailsEffect.NoteTitleChanged(noteTitle = newNoteTitle))
    }
}