package info.note.app

import io.github.vinceglb.filekit.PlatformFile
import java.io.File

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val filesDir: PlatformFile = createFilesDir()

    private fun createFilesDir(): PlatformFile {
        val directory = File(System.getProperty("java.io.tmpdir"))
        if (!directory.exists() || !directory.isDirectory) {
            directory.mkdirs()
        }
        return PlatformFile(directory)
    }
}