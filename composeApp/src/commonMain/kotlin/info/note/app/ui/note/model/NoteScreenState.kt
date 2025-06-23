package info.note.app.ui.note.model

import info.note.app.feature.note.model.Note

data class NoteScreenState(
    val isLoading: Boolean = true,
    val noteList: List<Note> = emptyList()
)