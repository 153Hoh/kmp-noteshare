package info.note.app.ui.note

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import info.note.app.domain.model.Note
import info.note.app.toTimeString
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Note(
    note: Note,
    onNoteClicked: (String) -> Unit = {},
    onRemoveNoteClicked: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onNoteClicked(note.id) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (note.isImportant) {
                Icon(
                    modifier = Modifier.padding(start = 8.dp),
                    tint = Color.Yellow,
                    imageVector = Icons.Filled.Star,
                    contentDescription = ""
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.padding(start = 4.dp).weight(3f),
                        text = note.title,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = note.creationTime.toTimeString(),
                        textAlign = TextAlign.Right,
                        fontSize = 10.sp
                    )
                }
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = note.message,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            IconButton(onClick = { onRemoveNoteClicked(note.id) }) {
                Icon(Icons.Filled.Delete, contentDescription = "")
            }
        }
    }
}

@Composable
fun NoteList(
    noteList: List<Note>,
    onNoteClicked: (String) -> Unit = {},
    onRemoveNoteClicked: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(4.dp),
    ) {
        items(noteList) { note ->
            Note(
                note = note,
                onNoteClicked = onNoteClicked,
                onRemoveNoteClicked = onRemoveNoteClicked
            )
        }
    }
}

@Preview
@Composable
fun NoteListPreview() {
    MaterialTheme {
        NoteList(
            noteList = listOf(
                Note(
                    title = "ASD",
                    message = "Valami asd message",
                    creationTime = System.currentTimeMillis(),
                    isImportant = false
                ),
                Note(
                    title = "DAS",
                    message = "Valami das message",
                    creationTime = System.currentTimeMillis(),
                    isImportant = true
                )
            )
        )
    }
}