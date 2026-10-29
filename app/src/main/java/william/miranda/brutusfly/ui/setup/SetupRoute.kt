package william.miranda.brutusfly.ui.setup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun SetupRoute(
    modifier: Modifier = Modifier,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SetupScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
