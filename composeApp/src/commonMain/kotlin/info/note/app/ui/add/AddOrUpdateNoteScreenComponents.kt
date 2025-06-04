package info.note.app.ui.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    onSetTimeClicked: (Int, Int, Long) -> Unit = { _, _, _ -> }
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

        Column(
            modifier = Modifier.verticalScroll(scrollState).padding(bottom = 72.dp)
        ) {
            NoteCard(
                state = state,
                onTitleUpdated = onTitleUpdated,
                onMessageUpdate = onMessageUpdate
            )
            TimeCard(
                hour = state.hour,
                minute = state.minute,
                dateInMillis = state.dateInMillis,
                onSetTimeClicked = onSetTimeClicked
            )
        }
        BottomRow(
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background),
            addButtonTitle = state.buttonTitle,
            isImportant = state.isImportant,
            onAddNoteClicked = onAddNoteClicked,
            onImportantClicked = onImportantClicked
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeCard(
    hour: Int? = null,
    minute: Int? = null,
    dateInMillis: Long? = null,
    onSetTimeClicked: (Int, Int, Long) -> Unit = { _, _, _ -> }
) {
    val isTimeCardExpanded = remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                if (!isTimeCardExpanded.value) {
                    isTimeCardExpanded.value = !isTimeCardExpanded.value
                }
            }
            .padding(start = 12.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
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
                isExpanded = isTimeCardExpanded,
                timePickerState = timePickerState,
                datePickerState = datePickerState,
                currentTimeCalendar = currentTime,
                hour = hour,
                minute = minute,
                dateInMillis = dateInMillis,
                onSetTimeClicked = onSetTimeClicked
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableTimePickerWithDatePicker(
    isExpanded: MutableState<Boolean> = mutableStateOf(false),
    timePickerState: TimePickerState = rememberTimePickerState(),
    datePickerState: DatePickerState = rememberDatePickerState(),
    currentTimeCalendar: Calendar = Calendar.getInstance(),
    hour: Int? = null,
    minute: Int? = null,
    dateInMillis: Long? = null,
    onSetTimeClicked: (Int, Int, Long) -> Unit = { _, _, _ -> }
) {
    val isDateExpanded = remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = !isExpanded.value,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
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
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
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

            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp, end = 4.dp),
                    onClick = {
                        if (isExpanded.value) {
                            isExpanded.value = !isExpanded.value
                        }
                    }
                ) {
                    Text("Cancel")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp, end = 4.dp),
                    onClick = {
                        onSetTimeClicked(
                            timePickerState.hour,
                            timePickerState.minute,
                            datePickerState.selectedDateMillis ?: dateInMillis
                            ?: currentTimeCalendar.timeInMillis
                        )
                        isExpanded.value = !isExpanded.value
                    },
                ) {
                    Text("Set")
                }
            }
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
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
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
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
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
fun NoteCard(
    state: AddOrUpdateNoteScreenViewModel.AddNoteScreenState = AddOrUpdateNoteScreenViewModel.AddNoteScreenState(),
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
                value = state.title,
                label = { Text("Title") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
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
                value = state.message,
                label = { Text("Message") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                onValueChange = onMessageUpdate
            )
        }
    }
}

@Composable
fun BottomRow(
    modifier: Modifier = Modifier,
    addButtonTitle: String = "Add note",
    isImportant: Boolean = false,
    onImportantClicked: () -> Unit = {},
    onAddNoteClicked: () -> Unit = {}
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onAddNoteClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background
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
            onClick = onImportantClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background
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