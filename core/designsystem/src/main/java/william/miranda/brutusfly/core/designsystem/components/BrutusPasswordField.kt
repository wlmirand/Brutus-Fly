package william.miranda.brutusfly.core.designsystem.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import william.miranda.brutusfly.core.designsystem.R
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreview
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreviews

@Composable
fun BrutusPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var visible by remember {
        mutableStateOf(false)
    }

    BrutusTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        enabled = enabled,
        isError = isError,
        supportingText = supportingText,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false
        ),
        keyboardActions = keyboardActions,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { visible = !visible },
                enabled = enabled
            ) {
                Icon(
                    imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                    contentDescription = stringResource(if (visible) R.string.brutus_password_hide else R.string.brutus_password_show)
                )
            }
        }
    )
}

@BrutusPreviews
@Composable
private fun BrutusPasswordFieldPreview() {
    BrutusPreview {
        var token by remember {
            mutableStateOf("preview-token-not-a-real-secret")
        }
        BrutusPasswordField(
            value = "",
            onValueChange = {},
            label = "Personal access token"
        )
        BrutusPasswordField(
            value = token,
            onValueChange = { token = it },
            label = "Personal access token"
        )
        BrutusPasswordField(
            value = "invalid-preview-token",
            onValueChange = {},
            label = "Personal access token",
            isError = true,
            supportingText = "The server rejected this token."
        )
        BrutusPasswordField(
            value = "preview-token-not-a-real-secret",
            onValueChange = {},
            label = "Personal access token",
            enabled = false
        )
    }
}
