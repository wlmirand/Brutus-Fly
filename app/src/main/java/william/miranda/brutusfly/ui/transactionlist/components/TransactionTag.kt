package william.miranda.brutusfly.ui.transactionlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme

private val TagShape = GenericShape { size, _ ->
    val tipWidth = minOf(size.height * 0.35f, size.width * 0.25f)
    moveTo(0f, size.height / 2f)
    lineTo(tipWidth, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width, size.height)
    lineTo(tipWidth, size.height)
    close()
}

/** Compact, non-interactive label for a transaction's API tag. */
@Composable
internal fun TransactionTag(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = TagShape
            )
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(size = 4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.45f),
                    shape = CircleShape
                )
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionTagPreview() {
    BrutusFlyTheme {
        TransactionTag(text = "Travel")
    }
}

@Preview(showBackground = true)
@Composable
private fun TransactionTagDarkPreview() {
    BrutusFlyTheme(darkTheme = true) {
        TransactionTag(text = "Travel")
    }
}
