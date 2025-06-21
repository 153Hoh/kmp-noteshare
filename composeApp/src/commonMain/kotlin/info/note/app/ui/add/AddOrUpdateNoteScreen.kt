package info.note.app.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddOrUpdateNoteScreen(
    viewModel: AddOrUpdateNoteScreenViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPermissionScreen: () -> Unit,
    onShowSnackBar: (String) -> Unit,
    onNoteTitleChanged: (String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                AddOrUpdateNoteScreenViewModel.AddNoteScreenEffect.NavigateBack -> onNavigateBack()
                is AddOrUpdateNoteScreenViewModel.AddNoteScreenEffect.ShowError -> onShowSnackBar(it.message)
                AddOrUpdateNoteScreenViewModel.AddNoteScreenEffect.PermissionRequired -> onNavigateToPermissionScreen()
                is AddOrUpdateNoteScreenViewModel.AddNoteScreenEffect.NoteTitleChanged -> onNoteTitleChanged(it.noteTitle)
            }
        }
    }

    AddOrUpdateNoteScreenContent(
        state = state.value,
        onTitleUpdated = {
            viewModel.onEvent(
                AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.OnTitleUpdated(
                    it
                )
            )
        },
        onMessageUpdate = {
            viewModel.onEvent(
                AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.OnMessageUpdated(
                    it
                )
            )
        },
        onAddNoteClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.AddNoteEvent)
        },
        onImportantClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.ImportantClicked)
        },
        onSetTimeClicked = { hour, minute, date ->
            viewModel.onEvent(
                AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.SetTimeEvent(
                    hour,
                    minute,
                    date
                )
            )
        },
        isCameraAvailable = viewModel.isCameraAvailable(),
        isGalleryAvailable = viewModel.isGalleryAvailable(),
        onAddFromGalleryClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.AddImageFromGalleryClicked)
        },
        onAddFromCameraClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.AddImageFromCameraClicked)
        },
        onSetImageClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.SetImageEvent)
        },
        onRemoveImageClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.RemoveImage)
        },
        onImageClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.ImageClicked)
        },
        onCloseHighLightClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.CloseImageHighlight)
        },
        onEditClicked = {
            viewModel.onEvent(AddOrUpdateNoteScreenViewModel.AddNoteScreenEvent.EditClicked)
        }
    )
}