package info.note.app.feature.sync.model

import kotlinx.serialization.Serializable

@Serializable
data class ConnectResponseBody(
    val syncKey: String
)