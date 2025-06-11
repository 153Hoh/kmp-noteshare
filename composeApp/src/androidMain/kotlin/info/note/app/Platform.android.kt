package info.note.app

import android.content.Context
import android.os.Build
import io.github.vinceglb.filekit.PlatformFile

class AndroidPlatform(
    context: Context
) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val filesDir: PlatformFile = PlatformFile(context.filesDir)
}