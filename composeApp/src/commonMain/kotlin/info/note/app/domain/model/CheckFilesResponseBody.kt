package info.note.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckFilesResponseBody(
    val downloadList: List<String>,
    val uploadList: List<String>
)