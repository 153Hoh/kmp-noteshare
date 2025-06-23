package info.note.app.ui.details.model

sealed class NoteDetailsEvent {
    data class OnTitleUpdated(val title: String) : NoteDetailsEvent()
    data class OnMessageUpdated(val message: String) : NoteDetailsEvent()
    data class SetTimeEvent(val hour: Int, val minute: Int, val dateInMillis: Long) :
        NoteDetailsEvent()

    data object ImportantClicked : NoteDetailsEvent()
    data object AddNoteEvent : NoteDetailsEvent()
    data object AddImageFromGalleryClicked : NoteDetailsEvent()
    data object AddImageFromCameraClicked : NoteDetailsEvent()
    data object SetImageEvent : NoteDetailsEvent()
    data object RemoveImage : NoteDetailsEvent()
    data object ImageClicked : NoteDetailsEvent()
    data object CloseImageHighlight : NoteDetailsEvent()
    data object EditClicked : NoteDetailsEvent()
}