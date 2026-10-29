package william.miranda.brutusfly.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import william.miranda.brutusfly.R
import william.miranda.brutusfly.core.designsystem.components.BrutusButton
import william.miranda.brutusfly.core.designsystem.components.BrutusErrorMessage
import william.miranda.brutusfly.core.designsystem.components.BrutusLoading
import william.miranda.brutusfly.core.designsystem.theme.BrutusTheme
import william.miranda.brutusfly.ui.accountlist.AccountListRoute
import william.miranda.brutusfly.ui.categorylist.CategoryListRoute
import william.miranda.brutusfly.ui.main.components.AppBottomBar
import william.miranda.brutusfly.ui.navigation.AppDestination
import william.miranda.brutusfly.ui.setup.SetupRoute
import william.miranda.brutusfly.ui.transactionlist.TransactionListRoute

@Composable
internal fun MainAppRoute(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (state) {
        MainState.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            BrutusLoading()
        }
        MainState.Setup -> SetupRoute(
            modifier = modifier
        )
        MainState.Ready -> ConfigurationReadyContent(
            modifier = modifier
        )
        MainState.ConfigurationError -> ConfigurationErrorContent(
            modifier = modifier,
            onRetry = {
                viewModel.onAction(
                    action = MainAction.Retry
                )
            },
            onReset = {
                viewModel.onAction(
                    action = MainAction.ResetConfiguration
                )
            }
        )
    }
}

@Composable
private fun ConfigurationReadyContent(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        AppDestination.Accounts
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        // MainActivity's Scaffold already applies the system insets.
        contentWindowInsets = WindowInsets(
            left = 0,
            top = 0,
            right = 0,
            bottom = 0
        ),
        bottomBar = {
            AppBottomBar(
                destinations = listOf(
                    AppDestination.Accounts,
                    AppDestination.Transactions,
                    AppDestination.Categories
                ),
                selectedDestination = backStack.lastOrNull() as? AppDestination ?: AppDestination.Accounts,
                onDestinationSelected = { destination ->
                    if (backStack.lastOrNull() != destination) {
                        while (backStack.size > 1) backStack.removeLastOrNull()
                        if (destination != AppDestination.Accounts) backStack.add(
                            destination
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier.fillMaxSize().padding(
                paddingValues = innerPadding
            ),
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) backStack.removeLastOrNull()
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<AppDestination.Accounts> {
                    AccountListRoute()
                }
                entry<AppDestination.Transactions> {
                    TransactionListRoute(
                        onChooseAccount = {
                            while (backStack.size > 1) backStack.removeLastOrNull()
                        }
                    )
                }

                entry<AppDestination.Categories> {
                    CategoryListRoute()
                }
            }
        )
    }
}

@Composable
private fun ConfigurationErrorContent(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().padding(
            all = BrutusTheme.spacing.screenHorizontal
        ),
        verticalArrangement = Arrangement.spacedBy(
            space = BrutusTheme.spacing.large,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BrutusErrorMessage(
            message = stringResource(
                id = R.string.configuration_read_error
            )
        )
        BrutusButton(
            label = stringResource(
                id = R.string.configuration_retry
            ),
            onClick = onRetry
        )
        BrutusButton(
            label = stringResource(
                id = R.string.configuration_reset
            ),
            onClick = onReset
        )
    }
}
