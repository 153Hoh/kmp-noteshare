package info.note.app.domain.model

data class UploadRequest(
    val name: String,
    val byteArray: ByteArray
)