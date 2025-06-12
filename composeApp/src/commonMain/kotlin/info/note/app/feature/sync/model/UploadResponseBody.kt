package info.note.app.feature.sync.model

import kotlinx.serialization.Serializable

@Serializable
data class UploadResponseBody(
    val uploadResultMap: Map<String, Boolean>
)