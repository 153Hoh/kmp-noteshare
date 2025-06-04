package info.note.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform