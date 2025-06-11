package info.note.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadResponseBody(
    val uploadResultMap: Map<String, Boolean>
)