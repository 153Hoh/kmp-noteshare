package info.note.app

import java.text.SimpleDateFormat
import java.util.Date


fun Long.toTimeString()  = SimpleDateFormat("yyyy. MM. dd. HH:mm").format(Date(this))
fun Long.toDateString()  = SimpleDateFormat("yyyy. MM. dd").format(Date(this))