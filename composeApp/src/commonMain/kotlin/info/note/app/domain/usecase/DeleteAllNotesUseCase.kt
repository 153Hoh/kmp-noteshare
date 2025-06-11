package info.note.app.domain.usecase

import info.note.app.domain.repository.file.FileRepository
import info.note.app.domain.repository.note.NoteRepository

class DeleteAllNotesUseCase(
    private val noteRepository: NoteRepository,
    private val fileRepository: FileRepository
) {

    suspend operator fun invoke() {
        val notes = noteRepository.getAllNotes()

        notes.forEach { note ->
            noteRepository.removeNote(note.noteId)
        }

        fileRepository.deleteAllNotes()
    }
}