package info.note.app.ui.note.model

sealed class NoteEffect {
    data class NavigateToNote(val noteId: String) : NoteEffect()
    data class ShowError(val message: String) : NoteEffect()
}