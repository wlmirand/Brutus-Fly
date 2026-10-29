package william.miranda.brutusfly.core.designsystem.preview

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import william.miranda.brutusfly.core.designsystem.theme.BrutusFlyTheme
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme

@Preview(name = "Light", showBackground = true, widthDp = 393, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark", showBackground = true, widthDp = 393, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
internal annotation class BrutusPreviews

/** Uses the preview's UI mode with the same colors, typography and spacing as the application. */
@Composable
internal fun BrutusPreview(
    content: @Composable ColumnScope.() -> Unit
) {
    BrutusFlyTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Column(
                modifier = Modifier.padding(
                    all = BrutusTheme.spacing.large
                ),
                verticalArrangement = Arrangement.spacedBy(
                    space = BrutusTheme.spacing.large
                ),
                content = content
            )
        }
    }
}
