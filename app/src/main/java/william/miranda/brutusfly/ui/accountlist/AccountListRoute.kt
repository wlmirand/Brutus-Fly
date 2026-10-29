package william.miranda.brutusfly.ui.accountlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun AccountListRoute(
    modifier: Modifier = Modifier,
    viewModel: AccountListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AccountListScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
