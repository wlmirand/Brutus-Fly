package william.miranda.brutusfly.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import william.miranda.brutusfly.MainDispatcherRule
import william.miranda.brutusfly.domain.repository.ServerConfigurationRepository
import william.miranda.brutusfly.domain.usecase.ClearServerConfigurationUseCase
import william.miranda.brutusfly.domain.usecase.ObserveServerConfigurationUseCase
import william.miranda.brutusfly.ui.main.MainAction
import william.miranda.brutusfly.ui.main.MainState
import william.miranda.brutusfly.ui.main.MainViewModel

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun routesFromLoadingToSetupAndThenToReadyAfterSaving() = runTest {
        val repository = TestRepository()
        val viewModel = MainViewModel(
            ObserveServerConfigurationUseCase(repository),
            ClearServerConfigurationUseCase(repository),
        )
        assertEquals(MainState.Loading, viewModel.state.value)
        runCurrent()
        assertEquals(MainState.Setup, viewModel.state.value)

        repository.isConfigured.value = true
        runCurrent()
        assertEquals(MainState.Ready, viewModel.state.value)

        viewModel.onAction(MainAction.ResetConfiguration)
        runCurrent()
        assertEquals(MainState.Setup, viewModel.state.value)
    }

    @Test
    fun readFailureShowsRecoveryAndResetAllowsSetupAgain() = runTest {
        var unreadable = true
        val repository = object : ServerConfigurationRepository {
            override val isConfigured = flow {
                if (unreadable) error("Unreadable configuration")
                emit(false)
            }
            override suspend fun configure(baseUrl: String, personalAccessToken: String) = Unit
            override suspend fun clear() {
                unreadable = false
            }
        }
        val viewModel = MainViewModel(
            ObserveServerConfigurationUseCase(repository),
            ClearServerConfigurationUseCase(repository),
        )
        runCurrent()
        assertEquals(MainState.ConfigurationError, viewModel.state.value)

        viewModel.onAction(MainAction.ResetConfiguration)
        runCurrent()

        assertEquals(MainState.Setup, viewModel.state.value)
    }

    private class TestRepository : ServerConfigurationRepository {
        override val isConfigured = MutableStateFlow(false)
        override suspend fun configure(baseUrl: String, personalAccessToken: String) {
            isConfigured.value = true
        }
        override suspend fun clear() {
            isConfigured.value = false
        }
    }
}
