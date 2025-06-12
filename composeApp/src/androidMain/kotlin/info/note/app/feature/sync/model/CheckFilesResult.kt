package info.note.app.feature.sync.model

data class CheckFilesResult(
    val downloadList: List<String>,
    val uploadList: List<String>
) {
    companion object {
        fun from(checkFilesResponseBody: CheckFilesResponseBody): CheckFilesResult =
            CheckFilesResult(
                downloadList = checkFilesResponseBody.downloadList,
                uploadList = checkFilesResponseBody.uploadList
            )
    }
}
