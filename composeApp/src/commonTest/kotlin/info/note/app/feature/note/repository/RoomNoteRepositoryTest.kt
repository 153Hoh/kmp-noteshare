package info.note.app.feature.note.repository

import androidx.room.Room
import info.note.app.createNoteEntity
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest

class RoomNoteRepositoryTest : StringSpec({

    lateinit var sut: RoomNoteRepository

    val databaseBuilder: DatabaseBuilder = mockk()

    beforeEach {
        sut = RoomNoteRepository(databaseBuilder)

        every { databaseBuilder.getDatabaseBuilder() } returns Room.inMemoryDatabaseBuilder()
    }

    afterEach {
        unmockkAll()
    }

    "AddNote adds note" {
        runTest {
            sut.addNote(createNoteEntity("noteId"))

            sut.getAllNotes().first().noteId shouldBe "noteId"
        }
    }

    "AddNote updates note if note already exists" {
        runTest {
            sut.addNote(createNoteEntity(noteId = "noteId", title = "ASD"))

            sut.addNote(createNoteEntity(noteId = "noteId", title = "DAS"))
            sut.getAllNotes().first().title shouldBe "DAS"
        }
    }

    "AddNote updates lastUpdateTime" {
        runTest {
            val currentUpdateTime = sut.lastUpdateTime()

            sut.addNote(createNoteEntity(noteId = "noteId", title = "ASD"))

            sut.lastUpdateTime() shouldNotBe currentUpdateTime
        }
    }

    "RemoveNote removes note" {
        runTest {
            sut.addNote(createNoteEntity(noteId = "noteId", title = "ASD"))

            sut.removeNote(noteId = "noteId")
            sut.getAllNotes().size shouldBe 0
        }
    }

    "FetchNoteDetails fetches note details" {
        runTest {
            val note = createNoteEntity(noteId = "noteId", title = "ASD")

            sut.addNote(note)

            val result = sut.fetchNoteDetails("noteId")
            result.isSuccess shouldBe true
            result.getOrNull()?.noteId shouldBe note.noteId
        }
    }

    "RefreshNotes refreshes notes" {
        runTest {
            sut.addNote(createNoteEntity(noteId = "noteId", title = "ASD"))
            sut.addNote(createNoteEntity(noteId = "noteId", title = "DAS"))

            sut.refreshNotes(listOf(createNoteEntity(title = "SAD")))

            sut.getAllNotes().first().title shouldBe "SAD"
        }
    }
})