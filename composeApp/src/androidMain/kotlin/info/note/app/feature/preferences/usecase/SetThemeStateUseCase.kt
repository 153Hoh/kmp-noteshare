package info.note.app.feature.preferences.usecase

import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.ui.theme.ThemeState

class SetThemeStateUseCase(
    private val preferencesRepository: PreferencesRepository
) {

    suspend operator fun invoke(themeState: ThemeState) {
        preferencesRepository.setRawThemeState(themeState.name)
    }
}