package william.miranda.brutusfly.ui.setup

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import william.miranda.brutusfly.MainDispatcherRule
import william.miranda.brutusfly.domain.model.ConfigurationException
import william.miranda.brutusfly.domain.model.ConfigurationFailure
import william.miranda.brutusfly.domain.repository.ServerConfigurationRepository
import william.miranda.brutusfly.domain.usecase.ConfigureServerUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class SetupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun saveShowsProgressPreventsDuplicateSubmissionsAndClearsTokenOnSuccess() = runTest {
        val response = CompletableDeferred<Unit>()
        var calls = 0
        val repository = repository { url, token ->
            calls++
            assertEquals("https://example.com/", url)
            assertEquals("secret-token", token)
            response.await()
        }
        val viewModel = filledViewModel(repository)

        viewModel.onAction(SetupAction.Save)
        runCurrent()

        assertTrue(viewModel.state.value.isSaving)
        viewModel.onAction(SetupAction.Save)
        viewModel.onAction(SetupAction.TokenChanged("changed"))
        runCurrent()
        assertEquals(1, calls)
        assertEquals("secret-token", viewModel.state.value.token)

        response.complete(Unit)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isSaving)
        assertEquals("", viewModel.state.value.token)
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun failureKeepsInputAndEditingClearsTheError() = runTest {
        val viewModel = filledViewModel(repository { _, _ ->
            throw ConfigurationException(ConfigurationFailure.UNAUTHORIZED)
        })

        viewModel.onAction(SetupAction.Save)
        advanceUntilIdle()

        assertEquals(ConfigurationFailure.UNAUTHORIZED, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isSaving)
        assertEquals("secret-token", viewModel.state.value.token)

        viewModel.onAction(SetupAction.TokenChanged("replacement"))

        assertNull(viewModel.state.value.error)
    }

    @Test
    fun stateStringDoesNotExposeCredentials() {
        val state = SetupState(baseUrl = "https://private.example.com/", token = "secret-token")
        assertFalse(state.toString().contains("secret-token"))
        assertFalse(state.toString().contains("private.example.com"))
    }

    private fun filledViewModel(repository: ServerConfigurationRepository): SetupViewModel =
        SetupViewModel(ConfigureServerUseCase(repository)).apply {
            onAction(SetupAction.UrlChanged("https://example.com/"))
            onAction(SetupAction.TokenChanged("secret-token"))
        }

    private fun repository(save: suspend (String, String) -> Unit) = object : ServerConfigurationRepository {
        override val isConfigured = MutableStateFlow(false)
        override suspend fun configure(baseUrl: String, personalAccessToken: String) {
            save(baseUrl, personalAccessToken)
            isConfigured.value = true
        }
        override suspend fun clear() {
            isConfigured.value = false
        }
    }
}
