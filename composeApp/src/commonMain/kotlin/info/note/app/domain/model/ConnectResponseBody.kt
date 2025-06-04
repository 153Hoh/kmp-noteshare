package info.note.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ConnectResponseBody(
    val syncKey: String
)