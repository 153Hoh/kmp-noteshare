package info.note.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import info.note.app.ui.details.NoteDetailsScreen
import info.note.app.ui.note.NoteScreen
import info.note.app.ui.settings.Settings
import info.note.app.ui.theme.ThemeState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
sealed class NoteScreens {
    @Serializable
    data object NoteScreen : NoteScreens()

    @Serializable
    data class NoteDetailsScreen(val noteId: String? = null) : NoteScreens()

    @Serializable
    data object Settings : NoteScreens()

    @Serializable
    data object PermissionScreen : NoteScreens()
}

@Composable
fun NoteApp(
    modifier: Modifier = Modifier,
    themeState: ThemeState = ThemeState.AUTO,
    navController: NavHostController = rememberNavController(),
    viewModel: NoteAppViewModel = koinViewModel(),
    settingsContent: @Composable () -> Unit = {},
    permissionScreen: @Composable (onConfirmClicked: () -> Unit) -> Unit = {},
    onThemeStateChanged: (ThemeState) -> Unit = {}
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                is NoteAppViewModel.NoteAppEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            NoteAppBar(
                title = state.value.topBarTitle,
                canNavigateBack = !state.value.isOnHomeScreen,
                isOnHomeScreen = state.value.isOnHomeScreen,
                themeState = themeState,
                navigateUp = { navController.navigateUp() },
                navigateToSettings = { navController.navigate(NoteScreens.Settings) },
                onThemeStateChanged = onThemeStateChanged
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NoteScreens.NoteScreen,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable<NoteScreens.NoteScreen> {
                viewModel.onEvent(
                    NoteAppViewModel.NoteAppEvent.UpdateTopBar(
                        title = "NoteShare",
                        isOnHomeScreen = true
                    )
                )
                NoteScreen(
                    onNavigateToNote = { navController.navigate(NoteScreens.NoteDetailsScreen(it)) },
                    onNavigateToAddNote = { navController.navigate(NoteScreens.NoteDetailsScreen()) },
                    onShowSnackBar = {
                        viewModel.onEvent(NoteAppViewModel.NoteAppEvent.ShowSnackBar(it))
                    }
                )
            }
            composable<NoteScreens.NoteDetailsScreen> {
                NoteDetailsScreen(
                    onNavigateBack = { navController.navigate(NoteScreens.NoteScreen) },
                    onShowSnackBar = {
                        viewModel.onEvent(NoteAppViewModel.NoteAppEvent.ShowSnackBar(it))
                    },
                    onNavigateToPermissionScreen = { navController.navigate(NoteScreens.PermissionScreen) },
                    onNoteTitleChanged = { noteTitle ->
                        viewModel.onEvent(
                            NoteAppViewModel.NoteAppEvent.UpdateTopBar(
                                title = noteTitle,
                                isOnHomeScreen = false
                            )
                        )
                    }
                )
            }
            composable<NoteScreens.Settings> {
                viewModel.onEvent(
                    NoteAppViewModel.NoteAppEvent.UpdateTopBar(
                        title = "Settings",
                        isOnHomeScreen = false
                    )
                )
                Settings(content = settingsContent)
            }
            composable<NoteScreens.PermissionScreen> {
                viewModel.onEvent(
                    NoteAppViewModel.NoteAppEvent.UpdateTopBar(
                        title = "",
                        isOnHomeScreen = false
                    )
                )
                permissionScreen { navController.navigateUp() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteAppBar(
    title: String = "",
    canNavigateBack: Boolean,
    isOnHomeScreen: Boolean,
    themeState: ThemeState,
    navigateUp: () -> Unit,
    navigateToSettings: () -> Unit,
    onThemeStateChanged: (ThemeState) -> Unit
) {
    val isThemeStateSelectorExpanded = remember { mutableStateOf(false) }

    TopAppBar(
        title = { Text(title) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
            }
        },
        actions = {
            if (isOnHomeScreen) {
                IconButton(onClick = navigateToSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = ""
                    )
                }

                ThemeChangerIcon(
                    isExpanded = isThemeStateSelectorExpanded,
                    themeState = themeState,
                    onThemeStateChanged = onThemeStateChanged
                )

            }
        }
    )
}

@Composable
fun ThemeChangerIcon(
    isExpanded: MutableState<Boolean> = mutableStateOf(false),
    themeState: ThemeState = ThemeState.AUTO,
    onThemeStateChanged: (ThemeState) -> Unit
) {
    IconButton(onClick = { isExpanded.value = true }) {
        Icon(
            contentDescription = "",
            imageVector = when (themeState) {
                ThemeState.DARK -> Icons.Outlined.DarkMode
                ThemeState.LIGHT -> Icons.Outlined.LightMode
                ThemeState.AUTO -> if (isSystemInDarkTheme()) {
                    Icons.Outlined.DarkMode
                } else {
                    Icons.Outlined.LightMode
                }
            }
        )
    }

    DropdownMenu(
        expanded = isExpanded.value,
        onDismissRequest = { isExpanded.value = false }
    ) {
        DropdownMenuItem(
            text = { Text("Auto") },
            trailingIcon = {
                if (themeState == ThemeState.AUTO) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = ""
                    )
                }
            },
            onClick = {
                onThemeStateChanged(ThemeState.AUTO)
                isExpanded.value = false
            }
        )
        DropdownMenuItem(
            text = { Text("Light") },
            trailingIcon = {
                if (themeState == ThemeState.LIGHT) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = ""
                    )
                }
            },
            onClick = {
                onThemeStateChanged(ThemeState.LIGHT)
                isExpanded.value = false
            }
        )
        DropdownMenuItem(
            text = { Text("Dark") },
            trailingIcon = {
                if (themeState == ThemeState.DARK) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = ""
                    )
                }
            },
            onClick = {
                onThemeStateChanged(ThemeState.DARK)
                isExpanded.value = false
            }
        )
    }
}