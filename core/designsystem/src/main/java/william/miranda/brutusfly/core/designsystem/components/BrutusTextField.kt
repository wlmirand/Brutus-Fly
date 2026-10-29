package william.miranda.brutusfly.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreview
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreviews

@Composable
fun BrutusTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    placeholder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        isError = isError,
        supportingText = if (supportingText != null) {
            { Text(text = supportingText) }
        } else null,
        placeholder = if (placeholder != null) {
            { Text(text = placeholder) }
        } else null,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = MaterialTheme.shapes.medium
    )
}

@BrutusPreviews
@Composable
private fun BrutusTextFieldPreview() {
    BrutusPreview {
        var value by remember {
            mutableStateOf("")
        }
        BrutusTextField(
            value = value,
            onValueChange = { value = it },
            label = "Server URL",
            placeholder = "https://firefly.example.com/",
            supportingText = "Enter the installation root URL."
        )
        BrutusTextField(
            value = "https://firefly.example.com/",
            onValueChange = {},
            label = "Server URL"
        )
        BrutusTextField(
            value = "http://firefly.example.com/",
            onValueChange = {},
            label = "Server URL",
            isError = true,
            supportingText = "Use HTTPS to protect your connection."
        )
        BrutusTextField(
            value = "https://firefly.example.com/",
            onValueChange = {},
            label = "Server URL",
            enabled = false
        )
    }
}
