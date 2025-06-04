package info.note.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponseBody(
    val noteList: List<Note>
)