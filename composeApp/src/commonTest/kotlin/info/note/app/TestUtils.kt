package info.note.app

import info.note.app.feature.note.model.Note
import info.note.app.feature.note.repository.NoteEntity
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain

fun createNoteEntity(
    noteId: String = "noteId",
    title: String = "Title",
    message: String = "Message",
    creationTime: Long = 123L,
    isImportant: Boolean = false,
    dueDate: Long = 0L,
    imageId: String = ""
): NoteEntity = NoteEntity(
    noteId = noteId,
    title = title,
    message = message,
    creationTime = creationTime,
    isImportant = isImportant,
    dueDate = dueDate,
    imageId = imageId
)

fun createNote(
    noteId: String = "noteId",
    title: String = "Title",
    message: String = "Message",
    creationTime: Long = 123L,
    isImportant: Boolean = false,
    dueDate: Long = 0L,
    imageId: String = ""
): Note = Note(
    id = noteId,
    title = title,
    message = message,
    creationTime = creationTime,
    isImportant = isImportant,
    dueDate = dueDate,
    imageId = imageId
)

class CoroutineTestListener : TestListener {
    override suspend fun beforeTest(testCase: TestCase) {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    override suspend fun afterTest(testCase: TestCase, result: TestResult) {
        Dispatchers.resetMain()
    }
}

class MainCoroutineListener(
) : TestListener {
    override suspend fun beforeSpec(spec: Spec) {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    override suspend fun afterSpec(spec: Spec) {
        Dispatchers.resetMain()

    }
}