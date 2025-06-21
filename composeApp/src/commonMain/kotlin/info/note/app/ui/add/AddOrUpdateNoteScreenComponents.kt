package info.note.app.ui.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import info.note.app.noteShareEnterTransition
import info.note.app.noteShareExitTransition
import info.note.app.toDateString
import info.note.app.toTimeString
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.util.Calendar

@Composable
fun AddOrUpdateNoteScreenContent(
    state: AddOrUpdateNoteScreenViewModel.AddNoteScreenState = AddOrUpdateNoteScreenViewModel.AddNoteScreenState(),
    onTitleUpdated: (String) -> Unit = {},
    onMessageUpdate: (String) -> Unit = {},
    onImportantClicked: () -> Unit = {},
    onAddNoteClicked: () -> Unit = {},
    onSetTimeClicked: (Int, Int, Long) -> Unit = { _, _, _ -> },
    isGalleryAvailable: Boolean = true,
    isCameraAvailable: Boolean = true,
    onAddFromGalleryClicked: () -> Unit = {},
    onAddFromCameraClicked: () -> Unit = {},
    onRemoveImageClicked: () -> Unit = {},
    onSetImageClicked: () -> Unit = {},
    onImageClicked: () -> Unit = {},
    onCloseHighLightClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        val scrollState = rememberScrollState()

        LaunchedEffect(scrollState.maxValue) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }

        Box {
            Column(
                modifier = Modifier.verticalScroll(scrollState).padding(bottom = 72.dp)
            ) {
                NoteCard(
                    isInEditMode = state.noteState != AddOrUpdateNoteScreenViewModel.NoteState.READ,
                    title = state.title,
                    message = state.message,
                    onTitleUpdated = onTitleUpdated,
                    onMessageUpdate = onMessageUpdate
                )
                TimeCard(
                    isInEditMode = state.noteState != AddOrUpdateNoteScreenViewModel.NoteState.READ,
                    hour = state.hour,
                    minute = state.minute,
                    dateInMillis = state.dateInMillis,
                    onSetTimeClicked = onSetTimeClicked,
                    onEditClicked = onEditClicked
                )
                ImageCard(
                    isInEditMode = state.noteState != AddOrUpdateNoteScreenViewModel.NoteState.READ,
                    image = state.image?.bitmap ?: state.tempImage?.bitmap,
                    isGalleryAvailable = isGalleryAvailable,
                    isCameraAvailable = isCameraAvailable,
                    onAddFromGalleryClicked = onAddFromGalleryClicked,
                    onAddFromCameraClicked = onAddFromCameraClicked,
                    onRemoveImageClicked = onRemoveImageClicked,
                    onSetImageClicked = onSetImageClicked,
                    onImageClicked = onImageClicked,
                    onEditClicked = onEditClicked
                )
            }
            if (state.highlightImage) {
                ImageHighLightBox(
                    image = state.image?.bitmap ?: state.tempImage?.bitmap,
                    onCloseHighLightClicked = onCloseHighLightClicked
                )
            }
        }
        BottomRow(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background),
            noteState = state.noteState,
            addButtonTitle = state.noteState.title,
            isImportant = state.isImportant,
            onAddNoteClicked = onAddNoteClicked,
            onImportantClicked = onImportantClicked,
            onEditClicked = onEditClicked
        )

        if (state.isLoading) {
            LoadingBox()
        }
    }
}

@Composable
fun LoadingBox() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Text(text = "Loading note details...", color = Color.White)
        }
    }
}

@Composable
fun ImageHighLightBox(
    image: ImageBitmap? = null,
    onCloseHighLightClicked: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 72.dp)
            .background(Color.Black.copy(alpha = 0.4f)),
    ) {
        if (image != null) {
            Image(
                modifier = Modifier
                    .padding(24.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .align(Alignment.Center),
                bitmap = image,
                contentDescription = "",
            )
        } else {
            Text(modifier = Modifier.align(Alignment.Center), text = "Image not available!")
        }
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clickable { onCloseHighLightClicked() },
            imageVector = Icons.Filled.Close,
            tint = Color.White,
            contentDescription = ""
        )
    }
}

@Composable
fun ImageCard(
    image: ImageBitmap? = null,
    isInEditMode: Boolean = false,
    isGalleryAvailable: Boolean = true,
    isCameraAvailable: Boolean = true,
    onAddFromGalleryClicked: () -> Unit = {},
    onAddFromCameraClicked: () -> Unit = {},
    onRemoveImageClicked: () -> Unit = {},
    onSetImageClicked: () -> Unit = {},
    onImageClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    val isImageCardExpanded = remember { mutableStateOf(false) }

    LaunchedEffect(image, isInEditMode) {
        if (image != null && !isInEditMode) {
            isImageCardExpanded.value = true
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                if (image == null && !isInEditMode) {
                    onEditClicked()
                }
                if (!isImageCardExpanded.value) {
                    isImageCardExpanded.value = !isImageCardExpanded.value
                }
            }
            .padding(start = 12.dp, top = 12.dp, end = 12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = !isImageCardExpanded.value,
                enter = noteShareEnterTransition(),
                exit = noteShareExitTransition()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (image == null) {
                        Icon(imageVector = Icons.Filled.Image, contentDescription = "")
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Add Image"
                        )
                    } else {
                        Icon(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            imageVector = Icons.Filled.Done,
                            tint = Color.Green,
                            contentDescription = ""
                        )
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = "Image"
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isImageCardExpanded.value,
                enter = noteShareEnterTransition(),
                exit = noteShareExitTransition()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = if (image != null) "Image" else "Add Image"
                    )

                    if (image != null) {
                        Box {
                            Image(
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(150.dp)
                                    .padding(16.dp)
                                    .clickable { onImageClicked() }
                                    .clip(RoundedCornerShape(5.dp)),
                                bitmap = image,
                                contentDescription = "",
                            )
                            if (isInEditMode) {
                                Icon(
                                    modifier = Modifier.align(Alignment.TopEnd)
                                        .clickable { onRemoveImageClicked() },
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = ""
                                )
                            }
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            if (!isCameraAvailable && !isGalleryAvailable) {
                                Text("Sorry you cannot add pictures")
                            }

                            if (isGalleryAvailable) {
                                Card(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(150.dp)
                                        .padding(16.dp)
                                        .clickable { onAddFromGalleryClicked() },
                                    elevation = CardDefaults.elevatedCardElevation()
                                ) {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = Icons.Filled.PhotoLibrary,
                                        contentDescription = ""
                                    )
                                }
                            }
                            if (isCameraAvailable) {
                                Card(
                                    modifier = Modifier
                                        .width(150.dp)
                                        .height(150.dp)
                                        .padding(16.dp)
                                        .clickable { onAddFromCameraClicked() },
                                    elevation = CardDefaults.elevatedCardElevation()
                                ) {
                                    Icon(
                                        modifier = Modifier.fillMaxSize(),
                                        imageVector = Icons.Filled.Camera,
                                        contentDescription = ""
                                    )
                                }
                            }
                        }
                    }

                    CardBottomButtonRow(
                        isInEditMode = isInEditMode,
                        setEnabled = image != null,
                        onCloseClicked = {
                            isImageCardExpanded.value = !isImageCardExpanded.value
                        },
                        onSetClicked = {
                            onSetImageClicked()
                            isImageCardExpanded.value = false
                        },
                        onEditClicked = onEditClicked
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeCard(
    isInEditMode: Boolean = false,
    hour: Int? = null,
    minute: Int? = null,
    dateInMillis: Long? = null,
    onSetTimeClicked: (Int, Int, Long) -> Unit = { _, _, _ -> },
    onEditClicked: () -> Unit = {}
) {
    val isTimeCardExpanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                if (hour != null && minute != null && dateInMillis != null && !isInEditMode) {
                    onEditClicked()
                }
                if (!isTimeCardExpanded.value) {
                    isTimeCardExpanded.value = !isTimeCardExpanded.value
                }
            }
            .padding(start = 12.dp, top = 12.dp, end = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val currentTime = Calendar.getInstance()

            val timePickerState = rememberTimePickerState(
                initialHour = hour ?: currentTime.get(Calendar.HOUR_OF_DAY),
                initialMinute = minute ?: currentTime.get(Calendar.MINUTE),
                is24Hour = true,
            )

            val datePickerState =
                rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

            ExpandableTimePickerWithDatePicker(
                isInEditMode = isInEditMode,
                isExpanded = isTimeCardExpanded,
                timePickerState = timePickerState,
                datePickerState = datePickerState,
                currentTimeCalendar = currentTime,
                hour = hour,
                minute = minute,
                dateInMillis = dateInMillis,
                onSetTimeClicked = onSetTimeClicked,
                onEditClicked = onEditClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableTimePickerWithDatePicker(
    isInEditMode: Boolean = false,
    isExpanded: MutableState<Boolean> = mutableStateOf(false),
    timePickerState: TimePickerState = rememberTimePickerState(),
    datePickerState: DatePickerState = rememberDatePickerState(),
    currentTimeCalendar: Calendar = Calendar.getInstance(),
    hour: Int? = null,
    minute: Int? = null,
    dateInMillis: Long? = null,
    onSetTimeClicked: (Int, Int, Long) -> Unit = { _, _, _ -> },
    onEditClicked: () -> Unit = {}
) {
    val isDateExpanded = remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !isExpanded.value,
        enter = noteShareEnterTransition(),
        exit = noteShareExitTransition()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (hour != null && minute != null) {
                Icon(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    imageVector = Icons.Filled.Done,
                    tint = Color.Green,
                    contentDescription = ""
                )

                val timeInMillis = Calendar.getInstance().apply {
                    timeInMillis = datePickerState.selectedDateMillis
                        ?: dateInMillis
                                ?: currentTimeCalendar.timeInMillis
                    set(Calendar.MINUTE, minute)
                    set(Calendar.HOUR_OF_DAY, hour)
                }.timeInMillis

                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Time set to: ${timeInMillis.toTimeString()}"
                )
            } else {
                Icon(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = ""
                )
                Text(modifier = Modifier.padding(8.dp), text = "Set time")
            }
        }
    }

    AnimatedVisibility(
        visible = isExpanded.value,
        enter = noteShareEnterTransition(),
        exit = noteShareExitTransition()
    ) {

        LaunchedEffect(datePickerState.selectedDateMillis) {
            if (isDateExpanded.value) {
                isDateExpanded.value = false
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimeInput(
                modifier = Modifier.padding(8.dp),
                state = timePickerState
            )

            ExpandableDatePicker(
                selectedDateInMillis = dateInMillis,
                isExpanded = isDateExpanded,
                currentTimeCalendar = currentTimeCalendar,
                datePickerState = datePickerState
            )

            CardBottomButtonRow(
                isInEditMode = isInEditMode,
                onCloseClicked = {
                    if (isExpanded.value) {
                        isExpanded.value = !isExpanded.value
                    }
                },
                onSetClicked = {
                    onSetTimeClicked(
                        timePickerState.hour,
                        timePickerState.minute,
                        datePickerState.selectedDateMillis ?: dateInMillis
                        ?: currentTimeCalendar.timeInMillis
                    )
                    isExpanded.value = !isExpanded.value
                },
                onEditClicked = onEditClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableDatePicker(
    selectedDateInMillis: Long? = null,
    isExpanded: MutableState<Boolean> = mutableStateOf(false),
    datePickerState: DatePickerState = rememberDatePickerState(),
    currentTimeCalendar: Calendar = Calendar.getInstance()
) {
    AnimatedVisibility(
        visible = !isExpanded.value,
        enter = noteShareEnterTransition(),
        exit = noteShareExitTransition()
    ) {
        Row(modifier = Modifier.clickable {
            isExpanded.value = !isExpanded.value
        }) {
            val date =
                datePickerState.selectedDateMillis ?: selectedDateInMillis
                ?: currentTimeCalendar.timeInMillis

            Icon(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                imageVector = Icons.Filled.Event,
                contentDescription = ""
            )

            Text(modifier = Modifier.padding(8.dp), text = date.toDateString())
        }
    }

    AnimatedVisibility(
        visible = isExpanded.value,
        enter = noteShareEnterTransition(),
        exit = noteShareExitTransition()
    ) {
        Box {
            DatePicker(
                colors = DatePickerDefaults.colors(
                    containerColor = CardDefaults.cardColors().containerColor
                ),
                state = datePickerState
            )
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd).padding(4.dp),
                onClick = { isExpanded.value = !isExpanded.value }
            ) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = "")
            }
        }
    }
}

@Composable
fun CardBottomButtonRow(
    isInEditMode: Boolean = false,
    setEnabled: Boolean = true,
    onCloseClicked: () -> Unit = {},
    onSetClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp, end = 4.dp),
            onClick = onCloseClicked
        ) {
            Text("Close")
        }
        Button(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp, end = 4.dp),
            onClick = {
                if (!isInEditMode) {
                    onEditClicked()
                } else {
                    onSetClicked()
                }
            },
            enabled = setEnabled
        ) {
            Text(text = if (isInEditMode) "Set" else "Edit")
        }
    }
}

@Composable
fun NoteCard(
    title: String,
    message: String,
    isInEditMode: Boolean = false,
    onTitleUpdated: (String) -> Unit = {},
    onMessageUpdate: (String) -> Unit = {},
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(start = 12.dp, top = 24.dp, end = 12.dp)
            .heightIn(max = 200.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                value = title,
                enabled = isInEditMode,
                label = { Text("Title") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    disabledTextColor = MaterialTheme.colorScheme.secondary
                ),
                onValueChange = onTitleUpdated
            )
            HorizontalDivider(
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 4.dp,
                    bottom = 4.dp,
                    end = 12.dp
                ), thickness = 2.dp
            )
            TextField(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp)
                    .fillMaxWidth(),
                value = message,
                enabled = isInEditMode,
                label = { Text("Message") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    disabledTextColor = MaterialTheme.colorScheme.secondary
                ),
                onValueChange = onMessageUpdate
            )
        }
    }
}

@Composable
fun BottomRow(
    noteState: AddOrUpdateNoteScreenViewModel.NoteState = AddOrUpdateNoteScreenViewModel.NoteState.ADD,
    modifier: Modifier = Modifier,
    addButtonTitle: String = "Add note",
    isImportant: Boolean = false,
    onImportantClicked: () -> Unit = {},
    onAddNoteClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { if (noteState == AddOrUpdateNoteScreenViewModel.NoteState.READ) onEditClicked() else onAddNoteClicked() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = addButtonTitle,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        Button(
            enabled = noteState != AddOrUpdateNoteScreenViewModel.NoteState.READ,
            onClick = onImportantClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isImportant) Icons.Filled.Star else Icons.Filled.StarOutline,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Important",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Preview
@Composable
fun TimeCardPreview() {
    MaterialTheme {
        TimeCard(
            hour = 12,
            minute = 13
        )
    }
}

@Preview
@Composable
fun AddOrUpdateNoteScreenPreview() {
    MaterialTheme {
        AddOrUpdateNoteScreenContent()
    }
}