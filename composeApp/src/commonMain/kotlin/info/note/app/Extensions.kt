package info.note.app

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date

fun Long.toTimeString() = SimpleDateFormat("yyyy. MM. dd. HH:mm").format(Date(this))
fun Long.toDateString() = SimpleDateFormat("yyyy. MM. dd").format(Date(this))

suspend fun PlatformFile.toFileIdWithName(): String {
    val md5 = MessageDigest.getInstance("MD5")
    md5.update(readBytes())
    md5.update(name.toByteArray())
    return md5.digest().joinToString("") { "%02x".format(it) }.plus("-noteImage")
}

fun noteShareEnterTransition(): EnterTransition =
    expandVertically(animationSpec = tween(durationMillis = 350)) + fadeIn(animationSpec = tween(durationMillis = 350))

fun noteShareExitTransition(): ExitTransition =
    shrinkVertically(animationSpec = tween(durationMillis = 350)) + fadeOut(animationSpec = tween(durationMillis = 350))