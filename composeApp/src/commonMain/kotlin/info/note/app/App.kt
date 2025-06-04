package info.note.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    modifier: Modifier = Modifier,
    settingsContent: @Composable () -> Unit = {}
) {
    MaterialTheme {
        NoteApp(
            settingsContent = settingsContent
        )
    }
}