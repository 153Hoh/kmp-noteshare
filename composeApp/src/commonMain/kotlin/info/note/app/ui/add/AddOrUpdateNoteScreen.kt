package info.note.app.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.note.app.rememberFlowWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddOrUpdateNoteScreen(
    viewModel: AddOrUpdateNoteScreenViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onShowSnackBar: (String) -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect = rememberFlowWithLifecycle(viewModel.effect)

    LaunchedEffect(effect) {
        effect.collect {
            when (it) {
                AddOrUpdateNoteScreenViewModel.AddNoteScreenEffect.NavigateBack -> onNavigateBack()
                is AddOrUpdateNoteScreenViewModel.AddNoteScreenEffect.ShowError -> onShowSnackBar(it.message)
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
        }
    )
}