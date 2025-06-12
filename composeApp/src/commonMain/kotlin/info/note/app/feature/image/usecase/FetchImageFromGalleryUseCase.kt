package info.note.app.feature.image.usecase

import info.note.app.feature.image.repository.ImagePickerRepository
import info.note.app.feature.image.model.ImageResult

class FetchImageFromGalleryUseCase(
    private val imagePickerRepository: ImagePickerRepository
) {

    suspend operator fun invoke(): Result<ImageResult> = imagePickerRepository.fetchFromGallery()
}