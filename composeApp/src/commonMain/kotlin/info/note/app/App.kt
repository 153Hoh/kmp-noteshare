package info.note.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import info.note.app.ui.theme.ThemeState
import info.note.app.ui.theme.darkScheme
import info.note.app.ui.theme.lightScheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    modifier: Modifier = Modifier,
    themeState: ThemeState = ThemeState.AUTO,
    onThemeStateChanged: (ThemeState) -> Unit = {},
    settingsContent: @Composable () -> Unit = {},
    permissionScreen: @Composable (onConfirmClicked: () -> Unit) -> Unit = {},
) {
    val colorScheme = when (themeState) {
        ThemeState.DARK -> darkScheme
        ThemeState.LIGHT -> lightScheme
        ThemeState.AUTO ->
            if (isSystemInDarkTheme()) {
                darkScheme
            } else {
                lightScheme
            }
    }

    Crossfade(targetState = colorScheme, label = "ThemeFade") {
        MaterialTheme(
            colorScheme = it
        ) {
            NoteApp(
                modifier = modifier,
                themeState = themeState,
                settingsContent = settingsContent,
                permissionScreen = permissionScreen,
                onThemeStateChanged = onThemeStateChanged
            )
        }
    }
}