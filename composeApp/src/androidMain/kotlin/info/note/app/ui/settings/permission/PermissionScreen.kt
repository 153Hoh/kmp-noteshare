package info.note.app.ui.settings.permission

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import info.note.app.CheckForPermission

@Composable
fun PermissionScreen(
    onConfirmClicked: () -> Unit
) {
    CheckForPermission(
        permissions = createPermissionList()
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "You have already granted all permissions!"
            )
            Button(onClick = onConfirmClicked) {
                Text("Confirm")
            }
        }
    }
}

fun createPermissionList(): List<String> {
    val permissionList = mutableListOf(Manifest.permission.CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissionList.add(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
    return permissionList
}