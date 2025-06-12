package info.note.app.feature.note.usecase

import info.note.app.feature.file.repository.FileRepository
import info.note.app.feature.note.repository.NoteRepository

class DeleteAllNotesUseCase(
    private val noteRepository: NoteRepository,
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke() {
        val notes = noteRepository.getAllNotes()

        notes.forEach { note ->
            noteRepository.removeNote(note.noteId)
        }

        fileRepository.deleteAllFiles()
    }
}