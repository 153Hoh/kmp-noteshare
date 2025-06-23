package info.note.app.ui.add.model

sealed class NoteDetailsEffect {
    data object NavigateBack : NoteDetailsEffect()
    data class ShowError(val message: String) : NoteDetailsEffect()
    data object PermissionRequired : NoteDetailsEffect()
    data class NoteTitleChanged(val noteTitle: String) : NoteDetailsEffect()
}