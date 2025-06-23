package info.note.app.ui.note.model

sealed class NoteScreenEvent {
    data class NoteClicked(val noteId: String) : NoteScreenEvent()
    data class RemoveNote(val noteId: String) : NoteScreenEvent()
}