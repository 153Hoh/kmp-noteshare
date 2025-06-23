package info.note.app.ui.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.note.app.ui.details.model.NoteDetailsEffect
import info.note.app.ui.details.model.NoteDetailsEvent
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NoteDetailsScreen(
    viewModel: NoteDetailsScreenViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPermissionScreen: () -> Unit,
    onShowSnackBar: (String) -> Unit,
    onNoteTitleChanged: (String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                NoteDetailsEffect.NavigateBack -> onNavigateBack()
                is NoteDetailsEffect.ShowError -> onShowSnackBar(it.message)
                NoteDetailsEffect.PermissionRequired -> onNavigateToPermissionScreen()
                is NoteDetailsEffect.NoteTitleChanged -> onNoteTitleChanged(it.noteTitle)
            }
        }
    }

    NoteDetailsScreenContent(
        state = state.value,
        onTitleUpdated = {
            viewModel.onEvent(
                NoteDetailsEvent.OnTitleUpdated(
                    it
                )
            )
        },
        onMessageUpdate = {
            viewModel.onEvent(
                NoteDetailsEvent.OnMessageUpdated(
                    it
                )
            )
        },
        onAddNoteClicked = {
            viewModel.onEvent(NoteDetailsEvent.AddNoteEvent)
        },
        onImportantClicked = {
            viewModel.onEvent(NoteDetailsEvent.ImportantClicked)
        },
        onSetTimeClicked = { hour, minute, date ->
            viewModel.onEvent(
                NoteDetailsEvent.SetTimeEvent(
                    hour,
                    minute,
                    date
                )
            )
        },
        isCameraAvailable = viewModel.isCameraAvailable(),
        isGalleryAvailable = viewModel.isGalleryAvailable(),
        onAddFromGalleryClicked = {
            viewModel.onEvent(NoteDetailsEvent.AddImageFromGalleryClicked)
        },
        onAddFromCameraClicked = {
            viewModel.onEvent(NoteDetailsEvent.AddImageFromCameraClicked)
        },
        onSetImageClicked = {
            viewModel.onEvent(NoteDetailsEvent.SetImageEvent)
        },
        onRemoveImageClicked = {
            viewModel.onEvent(NoteDetailsEvent.RemoveImage)
        },
        onImageClicked = {
            viewModel.onEvent(NoteDetailsEvent.ImageClicked)
        },
        onCloseHighLightClicked = {
            viewModel.onEvent(NoteDetailsEvent.CloseImageHighlight)
        },
        onEditClicked = {
            viewModel.onEvent(NoteDetailsEvent.EditClicked)
        }
    )
}