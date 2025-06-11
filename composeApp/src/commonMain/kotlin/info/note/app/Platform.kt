package info.note.app

import io.github.vinceglb.filekit.PlatformFile

interface Platform {
    val name: String

    val filesDir: PlatformFile
}