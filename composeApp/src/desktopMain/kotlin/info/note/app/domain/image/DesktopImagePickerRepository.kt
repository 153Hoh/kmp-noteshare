package info.note.app.domain.image

import info.note.app.domain.repository.image.ImagePickerCapabilities
import info.note.app.domain.repository.image.ImagePickerRepository
import info.note.app.domain.repository.image.ImageResult
import info.note.app.domain.repository.image.exception.CapabilityNotSupportedException
import info.note.app.domain.repository.image.exception.InvalidImageException
import info.note.app.domain.repository.image.toImageResult
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