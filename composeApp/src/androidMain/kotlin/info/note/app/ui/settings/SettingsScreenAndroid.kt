package info.note.app.ui.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import info.note.app.ui.settings.home.SettingsHomeScreen
import info.note.app.ui.settings.permission.PermissionScreen
import info.note.app.ui.settings.sync.SyncWithPcScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

enum class SettingsScreens {
    Home,
    SyncWithPc,
    Permission
}

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsScreenViewModel = koinViewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest {
            when (it) {
                is SettingsScreenViewModel.SettingsScreenEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        NavHost(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            navController = navController,
            startDestination = SettingsScreens.Home.name
        ) {
            composable(route = SettingsScreens.Home.name) {
                SettingsHomeScreen(
                    onNavigateToSyncWithPCScreen = { navController.navigate(SettingsScreens.SyncWithPc.name) },
                    onNavigateToPermissionScreen = { navController.navigate(SettingsScreens.Permission.name) }
                )
            }
            composable(route = SettingsScreens.SyncWithPc.name) {
                SyncWithPcScreen(
                    onNavigateBack = { navController.navigate(SettingsScreens.Home.name) },
                    onShowError = { message ->
                        viewModel.onEvent(
                            SettingsScreenViewModel.SettingsScreenEvent.ShowSnackBar(message)
                        )
                    }
                )
            }
            composable(route = SettingsScreens.Permission.name) {
                PermissionScreen(
                    onConfirmClicked = { navController.navigateUp() }
                )
            }
        }
    }
}