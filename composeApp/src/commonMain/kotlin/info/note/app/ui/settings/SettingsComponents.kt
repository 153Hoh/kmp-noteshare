package info.note.app.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import info.note.app.toTimeString
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun Setting(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Text(modifier = Modifier.padding(8.dp), text = title)
    }
}

@Composable
fun SyncStatus(
    modifier: Modifier = Modifier,
    isExpandedDefault: Boolean = false,
    status: Boolean = false,
    lastSyncTime: Long = 0L
) {
    val isExpanded = remember { mutableStateOf(isExpandedDefault) }

    Column(
        modifier.clickable {
            isExpanded.value = !isExpanded.value
        }
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(modifier = Modifier.padding(8.dp), text = "Sync status:")
            Icon(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                imageVector = Icons.Filled.Refresh,
                tint = if (status) Color.Green else Color.Red,
                contentDescription = ""
            )
            Icon(
                imageVector = if (!isExpanded.value) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                contentDescription = ""
            )
        }
        AnimatedVisibility(
            visible = isExpanded.value,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.padding(8.dp).fillMaxWidth()
            ) {
                Column(
                    modifier = modifier.padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            text = "Last Sync time:"
                        )
                        VerticalDivider()
                        Text(
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            text = if (lastSyncTime == 0L) "-" else lastSyncTime.toTimeString()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlreadySyncingCard(
    onCancelClicked: () -> Unit = {},
    onDisconnectClicked: () -> Unit = {}
) {
    Card(
        modifier = Modifier.padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.padding(4.dp).fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                text = "You are already syncing with a device!"
            )
            HorizontalDivider()
            Text(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                textAlign = TextAlign.Center,
                text = "Do you want remove the device and pair a new one?"
            )
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.weight(1f).padding(4.dp),
                    onClick = onCancelClicked
                ) {
                    Text("Cancel")
                }
                Button(
                    modifier = Modifier.weight(1f).padding(4.dp),
                    onClick = onDisconnectClicked
                ) {
                    Text("Disconnect")
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    onConfirmClicked: () -> Unit = {},
    onClose: () -> Unit = {}
) {
    Card(modifier = modifier.padding(16.dp), elevation = CardDefaults.elevatedCardElevation()) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                text = title
            )
            HorizontalDivider()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = message,
                textAlign = TextAlign.Center
            )
            Row {
                TextButton(onClose) {
                    Text("Close")
                }
                TextButton(onConfirmClicked) {
                    Text("Confirm")
                }
            }
        }
    }
}

@Preview
@Composable
fun ConfirmationDialogPreview() {
    MaterialTheme {
        ConfirmationDialog(
            title = "Disable note sync",
            message = "Are you sure you want to disable syncing?"
        )
    }
}

@Composable
@Preview
fun AlreadySyncingCardPreview() {
    MaterialTheme {
        AlreadySyncingCard()
    }
}

@Preview
@Composable
fun SyncStatusPreview() {
    MaterialTheme {
        Column {
            SyncStatus(
                status = true,
                isExpandedDefault = true,
                lastSyncTime = System.currentTimeMillis()
            )
            SyncStatus(status = false)
        }
    }
}

@Preview
@Composable
fun SettingPreview() {
    MaterialTheme {
        Setting("Valami setting")
    }
}