package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.ui.theme.ThemeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FetchThemeStateUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    operator fun invoke(): Flow<ThemeState> =
        preferencesRepository.getRawThemeState().map { ThemeState.valueOf(it) }
}