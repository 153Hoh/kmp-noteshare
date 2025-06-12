package info.note.app.feature.sync.usecase

import com.diamondedge.logging.logging
import info.note.app.feature.preferences.repository.PreferencesRepository
import info.note.app.feature.note.model.Note
import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.sync.repository.SyncRepository

class SyncNotesUseCase(
    private val syncRepository: SyncRepository,
    private val fileRepository: FileRepository,
    private val preferencesRepository: PreferencesRepository
) {

    private var uploadRetries = 0
    private var downloadRetries = 0

    suspend operator fun invoke(noteList: List<Note>): Result<List<Note>> {
        preferencesRepository.setLastSyncTime(System.currentTimeMillis())

        return syncRepository.sync(noteList).onSuccess {
            handleImageFiles(noteList)
        }
    }

    private suspend fun handleImageFiles(noteList: List<Note>) {
        val fileIds = noteList.map { it.imageId }

        if (fileIds.isEmpty()) {
            return
        }

        syncRepository.checkFileIds(fileIds).onSuccess { checkFilesResult ->
            handleUpload(noteList, checkFilesResult.uploadList)
            handleDownload(checkFilesResult.downloadList)
        }.onFailure {
            it.printStackTrace()
            logging().error { "Error while uploading!" }
        }
    }

    private suspend fun handleUpload(noteList: List<Note>, uploadList: List<String>) {
        val imageFileList =
            noteList
                .filter { uploadList.contains(it.imageId) }
                .mapNotNull { fileRepository.fetchFileById(it.imageId).getOrNull() }

        logging().info { "Uploading files (${imageFileList.size})" }

        if (imageFileList.isEmpty()) {
            return
        }

        syncRepository.uploadFiles(imageFileList).onSuccess { result ->
            result.uploadResultMap.forEach {
                logging().info { "Upload result: ${it.key} ${it.value}" }
            }
            if (uploadRetries < 3) {
                uploadRetries++
                handleUpload(
                    noteList,
                    result.uploadResultMap
                        .filter { !it.value }
                        .map { it.key }
                )
            }
        }.onFailure {
            it.printStackTrace()
            logging().error { "Error while downloading!" }
        }
    }

    private suspend fun handleDownload(downloadList: List<String>) {
        logging().info { "Downloading files (${downloadList.size})" }

        if (downloadList.isEmpty()) {
            return
        }

        syncRepository.downloadFiles(downloadList, fileRepository.parentFolder)
            .onSuccess { result ->
                result.downloadResultsMap.forEach {
                    logging().info { "download result: ${it.key} ${it.value}" }
                }
                if (downloadRetries < 3) {
                    downloadRetries++
                    handleDownload(
                        result.downloadResultsMap
                            .filter { !it.value }
                            .map { it.key }
                    )
                }
            }
    }


}