package info.note.app.feature.image.repository

import info.note.app.feature.image.model.ImageResult

interface ImagePickerRepository {

    val capabilities: List<ImagePickerCapabilities>

    suspend fun fetchFromGallery(): Result<ImageResult>
    suspend fun fetchFromCamera(): Result<ImageResult>
}