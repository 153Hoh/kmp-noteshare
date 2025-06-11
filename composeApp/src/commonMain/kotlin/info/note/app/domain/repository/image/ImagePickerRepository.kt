package info.note.app.domain.repository.image

interface ImagePickerRepository {

    val capabilities: List<ImagePickerCapabilities>

    suspend fun fetchFromGallery(): Result<ImageResult>
    suspend fun fetchFromCamera(): Result<ImageResult>
}