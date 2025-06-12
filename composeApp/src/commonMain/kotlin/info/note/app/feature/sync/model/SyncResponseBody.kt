package info.note.app.feature.sync.model

import info.note.app.feature.note.model.Note
import kotlinx.serialization.Serializable

@Serializable
data class SyncResponseBody(
    val noteList: List<Note>
)