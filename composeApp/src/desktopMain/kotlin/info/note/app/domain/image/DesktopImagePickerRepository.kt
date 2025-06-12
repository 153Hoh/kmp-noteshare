package info.note.app.domain.image

import info.note.app.feature.image.repository.ImagePickerCapabilities
import info.note.app.feature.image.repository.ImagePickerRepository
import info.note.app.feature.image.model.ImageResult
import info.note.app.feature.image.repository.exception.CapabilityNotSupportedException
import info.note.app.feature.image.repository.exception.InvalidImageException
import info.note.app.feature.image.model.toImageResult
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker

class DesktopImagePickerRepository : ImagePickerRepository {
    override val capabilities: List<ImagePickerCapabilities> =
        listOf(ImagePickerCapabilities.GALLERY)

    override suspend fun fetchFromGallery(): Result<ImageResult> = runCatching {
        val image = FileKit.openFilePicker(type = FileKitType.Image) ?: return Result.failure(
            InvalidImageException()
        )

        return Result.success(image.toImageResult())
    }

    override suspend fun fetchFromCamera(): Result<ImageResult> =
        Result.failure(CapabilityNotSupportedException())
}