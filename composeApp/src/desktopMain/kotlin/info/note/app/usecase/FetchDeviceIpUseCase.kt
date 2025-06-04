package info.note.app.usecase

import java.net.InetAddress

class FetchDeviceIpUseCase {

    operator fun invoke(): Result<String> = runCatching {
        InetAddress.getLocalHost().hostAddress
    }
}