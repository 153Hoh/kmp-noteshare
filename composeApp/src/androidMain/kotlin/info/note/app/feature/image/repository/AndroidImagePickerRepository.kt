package info.note.app.feature.image.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.ContextCompat
import info.note.app.feature.image.model.ImageResult
import info.note.app.feature.image.repository.exception.InvalidImageException
import info.note.app.feature.image.repository.exception.NoPermissionException
import info.note.app.feature.image.model.toImageResult
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitCameraType
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.uri
import java.io.File

class AndroidImagePickerRepository(
    private val context: Context
) : ImagePickerRepository {

    override val capabilities: List<ImagePickerCapabilities> =
        listOf(ImagePickerCapabilities.CAMERA, ImagePickerCapabilities.GALLERY)

    override suspend fun fetchFromGallery(): Result<ImageResult> = runCatching {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            throw NoPermissionException()
        }

        val image = FileKit.openFilePicker(type = FileKitType.Image) ?: return Result.failure(
            InvalidImageException()
        )

        val tempImageFile =
            createFileFromUri(image.uri) ?: return Result.failure(InvalidImageException())

        return Result.success(PlatformFile(tempImageFile).toImageResult())
    }

    override suspend fun fetchFromCamera(): Result<ImageResult> = runCatching {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            throw NoPermissionException()
        }

        val image =
            FileKit.openCameraPicker(type = FileKitCameraType.Photo) ?: return Result.failure(
                InvalidImageException()
            )

        val tempImageFile =
            createFileFromUri(image.uri) ?: return Result.failure(InvalidImageException())

        return Result.success(PlatformFile(tempImageFile).toImageResult())
    }

    private fun createFileFromUri(uri: Uri): File? {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("tempImage", ".jpg")
        return runCatching {
            tempFile.outputStream().use { fileOut ->
                inputStream?.copyTo(fileOut)
            }
            tempFile.deleteOnExit()
            inputStream?.close()
            tempFile
        }.getOrNull()
    }
}