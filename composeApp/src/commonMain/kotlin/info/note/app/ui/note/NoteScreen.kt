package info.note.app.ui.note

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import info.note.app.ui.note.model.NoteEffect
import info.note.app.ui.note.model.NoteScreenEvent
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NoteScreenViewModel = koinViewModel(),
    onNavigateToNote: (String) -> Unit = {},
    onNavigateToAddNote: () -> Unit = {},
    onShowSnackBar: (String) -> Unit = {}
) {

    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                is NoteEffect.NavigateToNote -> onNavigateToNote(it.noteId)
                is NoteEffect.ShowError -> onShowSnackBar(it.message)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        NoteList(
            noteList = state.value.noteList,
            onNoteClicked = { viewModel.onEvent(NoteScreenEvent.NoteClicked(it)) },
            onRemoveNoteClicked = {
                viewModel.onEvent(NoteScreenEvent.RemoveNote(it))
            }
        )
        FilledIconButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp, end = 24.dp),
            enabled = !state.value.isLoading,
            onClick = onNavigateToAddNote
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "")
        }
    }

    if (state.value.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Loading...")
        }
    }
}