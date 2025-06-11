package info.note.app.ui.settings

data class ConfirmationDialogState(
    val isShowing: Boolean = false,
    val title: String = "",
    val message: String = "",
    val onConfirmClicked: () -> Unit = {}
)