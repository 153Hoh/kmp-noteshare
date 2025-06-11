package info.note.app.domain.usecase

import info.note.app.domain.repository.image.ImagePickerRepository
import info.note.app.domain.repository.image.ImageResult

class FetchImageFromGalleryUseCase(
    private val imagePickerRepository: ImagePickerRepository
) {

    suspend operator fun invoke(): Result<ImageResult> = imagePickerRepository.fetchFromGallery()
}