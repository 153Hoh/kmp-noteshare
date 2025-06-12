package info.note.app.feature.image.usecase

import info.note.app.feature.image.repository.ImagePickerCapabilities
import info.note.app.feature.image.repository.ImagePickerRepository

class IsCameraImageAvailableUseCase(
    private val imagePickerRepository: ImagePickerRepository
) {

    operator fun invoke(): Boolean =
        imagePickerRepository.capabilities.contains(ImagePickerCapabilities.CAMERA)
}