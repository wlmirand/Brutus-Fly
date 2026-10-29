package william.miranda.brutusfly.ui.transactionlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun TransactionListRoute(
    onChooseAccount: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TransactionListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TransactionListScreen(
        state = state,
        onChooseAccount = onChooseAccount,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
