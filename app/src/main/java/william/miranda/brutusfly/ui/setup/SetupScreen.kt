package william.miranda.brutusfly.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.components.BrutusButton
import william.miranda.brutusfly.core.designsystem.components.BrutusErrorMessage
import william.miranda.brutusfly.core.designsystem.components.BrutusPasswordField
import william.miranda.brutusfly.core.designsystem.components.BrutusTextField
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationFailure

@Composable
internal fun SetupScreen(
    state: SetupState,
    onAction: (SetupAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val submit = {
        focusManager.clearFocus()
        onAction(
            SetupAction.Save
        )
    }
    val urlError = state.error == ConfigurationFailure.INVALID_URL ||
        state.error == ConfigurationFailure.HTTPS_REQUIRED
    val tokenError = state.error == ConfigurationFailure.INVALID_TOKEN ||
        state.error == ConfigurationFailure.UNAUTHORIZED
    val errorMessage = state.error?.let {
        configurationErrorMessage(
            error = it
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .imePadding()
            .verticalScroll(
                state = rememberScrollState()
            )
            .padding(
                horizontal = BrutusTheme.spacing.screenHorizontal,
                vertical = BrutusTheme.spacing.extraLarge
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.widthIn(
                max = 560.dp
            ).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                space = BrutusTheme.spacing.large
            )
        ) {
            Spacer(
                modifier = Modifier.height(
                    height = BrutusTheme.spacing.large
                )
            )
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(
                    size = 40.dp
                )
            )
            Text(
                text = stringResource(
                    id = R.string.setup_title
                ),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(
                    id = R.string.setup_description
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BrutusTextField(
                value = state.baseUrl,
                onValueChange = {
                    onAction(
                        SetupAction.UrlChanged(
                            value = it
                        )
                    )
                },
                label = stringResource(
                    id = R.string.setup_base_url
                ),
                placeholder = stringResource(
                    id = R.string.setup_url_hint
                ),
                enabled = !state.isSaving,
                isError = urlError,
                supportingText = if (urlError) errorMessage else null,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = false
                )
            )
            BrutusPasswordField(
                value = state.token,
                onValueChange = {
                    onAction(
                        SetupAction.TokenChanged(
                            value = it
                        )
                    )
                },
                label = stringResource(
                    id = R.string.setup_token
                ),
                enabled = !state.isSaving,
                isError = tokenError,
                supportingText = if (tokenError) errorMessage else null,
                keyboardActions = KeyboardActions(
                    onDone = { submit() }
                )
            )
            Text(
                text = stringResource(
                    id = R.string.setup_security_note
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (errorMessage != null && !urlError && !tokenError) {
                BrutusErrorMessage(
                    message = errorMessage
                )
            }
            BrutusButton(
                label = stringResource(
                    id = if (state.isSaving) R.string.setup_saving else R.string.setup_save
                ),
                onClick = submit,
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isSaving
            )
        }
    }
}

@Composable
private fun configurationErrorMessage(
    error: ConfigurationFailure
): String = stringResource(
    id = when (error) {
        ConfigurationFailure.INVALID_URL -> R.string.setup_error_url
        ConfigurationFailure.HTTPS_REQUIRED -> R.string.setup_error_https
        ConfigurationFailure.INVALID_TOKEN -> R.string.setup_error_token
        ConfigurationFailure.UNAUTHORIZED -> R.string.setup_error_unauthorized
        ConfigurationFailure.NETWORK -> R.string.setup_error_network
        ConfigurationFailure.SERVER -> R.string.setup_error_server
        ConfigurationFailure.INVALID_SERVER -> R.string.setup_error_invalid_server
        ConfigurationFailure.STORAGE -> R.string.setup_error_storage
        ConfigurationFailure.UNKNOWN -> R.string.setup_error_unknown
    }
)

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SetupPreview() {
    BrutusFlyTheme(
        darkTheme = false
    ) {
        SetupScreen(
            state = SetupState(),
            onAction = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SetupDarkPreview() {
    BrutusFlyTheme(
        darkTheme = true
    ) {
        SetupScreen(
            state = SetupState(
                error = ConfigurationFailure.NETWORK
            ),
            onAction = {}
        )
    }
}
