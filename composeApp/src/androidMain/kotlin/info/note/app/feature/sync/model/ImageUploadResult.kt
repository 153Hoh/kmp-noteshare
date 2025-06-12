package info.note.app.feature.sync.model

data class ImageUploadResult(
    val uploadResultMap: Map<String, Boolean>
) {

    companion object {
        fun from(responseBody: UploadResponseBody): ImageUploadResult =
            ImageUploadResult(responseBody.uploadResultMap)
    }
}