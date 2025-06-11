package info.note.app.domain.usecase

import info.note.app.domain.repository.image.ImagePickerCapabilities
import info.note.app.domain.repository.image.ImagePickerRepository

class IsCameraImageAvailableUseCase(
    private val imagePickerRepository: ImagePickerRepository
) {

    operator fun invoke(): Boolean =
        imagePickerRepository.capabilities.contains(ImagePickerCapabilities.CAMERA)
}