package info.note.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import info.note.app.ui.add.AddOrUpdateNoteScreen
import info.note.app.ui.note.NoteScreen
import info.note.app.ui.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
sealed class NoteScreens {
    @Serializable
    data object NoteScreen : NoteScreens()

    @Serializable
    data class AddOrUpdateNoteScreen(val noteId: String? = null) : NoteScreens()

    @Serializable
    data object Settings : NoteScreens()

    @Serializable
    data object PermissionScreen : NoteScreens()
}

@Composable
fun NoteApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: NoteAppViewModel = koinViewModel(),
    settingsContent: @Composable () -> Unit = {},
    permissionScreen: @Composable (onConfirmClicked: () -> Unit) -> Unit = {}
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val effect = rememberFlowWithLifecycle(viewModel.effect)
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(effect) {
        effect.collect {
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
                navigateUp = { navController.navigateUp() },
                navigateToSettings = { navController.navigate(NoteScreens.Settings) }
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
                    onNavigateToNote = { navController.navigate(NoteScreens.AddOrUpdateNoteScreen(it)) },
                    onNavigateToAddNote = { navController.navigate(NoteScreens.AddOrUpdateNoteScreen()) },
                    onShowSnackBar = {
                        viewModel.onEvent(NoteAppViewModel.NoteAppEvent.ShowSnackBar(it))
                    }
                )
            }
            composable<NoteScreens.AddOrUpdateNoteScreen> {
                viewModel.onEvent(
                    NoteAppViewModel.NoteAppEvent.UpdateTopBar(
                        title = "Add note",
                        isOnHomeScreen = false
                    )
                )
                AddOrUpdateNoteScreen(
                    onNavigateBack = { navController.navigate(NoteScreens.NoteScreen) },
                    onShowSnackBar = {
                        viewModel.onEvent(NoteAppViewModel.NoteAppEvent.ShowSnackBar(it))
                    },
                    onNavigateToPermissionScreen = { navController.navigate(NoteScreens.PermissionScreen) }
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
    navigateUp: () -> Unit,
    navigateToSettings: () -> Unit
) {
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
            }
        }
    )
}

@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
): Flow<T> {
    return remember(flow, lifecycleOwner) {
        flow.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }
}