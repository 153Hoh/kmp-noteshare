package info.note.app.feature.image.model

import androidx.compose.ui.graphics.ImageBitmap
import info.note.app.toFileIdWithName
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.absolutePath
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import kotlinx.serialization.Serializable

@Serializable
data class ImageResult(
    val fileId: String,
    val path: String,
    val bitmap: ImageBitmap
)

suspend fun PlatformFile.toImageResult(): ImageResult = ImageResult(
    fileId = toFileIdWithName(),
    path = absolutePath(),
    bitmap = toImageBitmap()
)