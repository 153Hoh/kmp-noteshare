package info.note.app.ui.settings.screen

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
import info.note.app.ui.settings.qr.ShownSyncQrScreen
import info.note.app.ui.settings.screen.model.SettingsScreenEffect
import info.note.app.ui.settings.screen.model.SettingsScreenEvent
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

enum class SettingsScreens {
    Home,
    ShowSyncQr
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
                is SettingsScreenEffect.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        NavHost(
            modifier = modifier.fillMaxSize().padding(it),
            navController = navController,
            startDestination = SettingsScreens.Home.name
        ) {
            composable(route = SettingsScreens.Home.name) {
                SettingsHomeScreen(
                    onNavigateToSyncShowQrClicked = { navController.navigate(SettingsScreens.ShowSyncQr.name) }
                )
            }
            composable(route = SettingsScreens.ShowSyncQr.name) {
                ShownSyncQrScreen(
                    onShowSnackBar = { message ->
                        viewModel.onEvent(
                            SettingsScreenEvent.ShowSnackBar(message)
                        )
                    },
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}

