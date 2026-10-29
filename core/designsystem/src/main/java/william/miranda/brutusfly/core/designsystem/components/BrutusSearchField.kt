package william.miranda.brutusfly.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.core.designsystem.R
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreview
import william.miranda.brutusfly.core.designsystem.preview.BrutusPreviews
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme

@Composable
fun BrutusSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.brutus_search_placeholder),
    clearContentDescription: String = stringResource(R.string.brutus_search_clear)
) {
    var isFocused by remember {
        mutableStateOf(false)
    }
    val isActive = isFocused || query.isNotEmpty()
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .onFocusChanged { isFocused = it.isFocused },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { focusManager.clearFocus() }
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = CircleShape)
                    .background(
                        color = if (isActive) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHighest
                        }
                    )
                    .then(
                        if (isActive) {
                            Modifier.border(
                                width = 1.5.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                        } else {
                            Modifier
                        }
                    )
                    .padding(
                        start = BrutusTheme.spacing.large,
                        end = BrutusTheme.spacing.small
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = if (isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(size = 22.dp)
                )

                Spacer(
                    modifier = Modifier.width(BrutusTheme.spacing.medium)
                )

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                    innerTextField()
                }

                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(size = 40.dp)
                            .clip(shape = CircleShape)
                            .clickable(
                                onClickLabel = clearContentDescription,
                                role = Role.Button,
                                onClick = { onQueryChange("") }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = clearContentDescription,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    )
}

@BrutusPreviews
@Composable
private fun BrutusSearchFieldPreview() {
    BrutusPreview {
        var query by remember {
            mutableStateOf("")
        }
        BrutusSearchField(
            query = query,
            onQueryChange = { query = it }
        )
        BrutusSearchField(
            query = "market",
            onQueryChange = {}
        )
    }
}
