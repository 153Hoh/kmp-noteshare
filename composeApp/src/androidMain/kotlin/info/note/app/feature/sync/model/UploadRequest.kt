package info.note.app.feature.sync.model

data class UploadRequest(
    val name: String,
    val byteArray: ByteArray
)