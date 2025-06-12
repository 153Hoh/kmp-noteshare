package info.note.app.feature.sync.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckFilesResponseBody(
    val downloadList: List<String>,
    val uploadList: List<String>
)