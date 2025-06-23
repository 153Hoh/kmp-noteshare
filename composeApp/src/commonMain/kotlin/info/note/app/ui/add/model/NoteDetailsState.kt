package info.note.app.ui.add.model

import info.note.app.feature.image.model.ImageResult

data class NoteDetailsState(
    val isLoading: Boolean = true,
    val title: String = "",
    val message: String = "",
    val hour: Int? = null,
    val minute: Int? = null,
    val dateInMillis: Long? = null,
    val isImportant: Boolean = false,
    val tempImage: ImageResult? = null,
    val image: ImageResult? = null,
    val highlightImage: Boolean = false,
    val noteState: NoteState = NoteState.ADD
)