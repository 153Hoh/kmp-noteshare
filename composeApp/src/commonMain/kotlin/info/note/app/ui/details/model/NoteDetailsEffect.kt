package info.note.app.ui.details.model

sealed class NoteDetailsEffect {
    data object NavigateBack : NoteDetailsEffect()
    data class ShowError(val message: String) : NoteDetailsEffect()
    data object PermissionRequired : NoteDetailsEffect()
    data class NoteTitleChanged(val noteTitle: String) : NoteDetailsEffect()
}