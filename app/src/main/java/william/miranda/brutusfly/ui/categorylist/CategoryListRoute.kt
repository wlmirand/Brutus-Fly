package william.miranda.brutusfly.ui.categorylist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun CategoryListRoute(
    modifier: Modifier = Modifier,
    viewModel: CategoryListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CategoryListScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier
    )
}
