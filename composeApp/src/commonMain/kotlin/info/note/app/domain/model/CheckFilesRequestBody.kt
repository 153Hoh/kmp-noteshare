package info.note.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckFilesRequestBody(
    val fileIds: List<String>
)