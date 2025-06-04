package info.note.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SyncRequestBody(
    val noteList: List<Note>
)
