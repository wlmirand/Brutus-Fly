package william.miranda.brutusfly.ui.accountlist

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import william.miranda.brutusfly.domain.model.Account
import william.miranda.brutusfly.domain.model.AccountFailure
import william.miranda.brutusfly.domain.model.AccountLoadException
import william.miranda.brutusfly.domain.model.AccountPage
import william.miranda.brutusfly.domain.repository.AccountRepository
import william.miranda.brutusfly.domain.usecase.GetAccountsUseCase
import william.miranda.brutusfly.ui.accountlist.mapper.AccountListUiMapper

@OptIn(ExperimentalCoroutinesApi::class)
class AccountListViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadsAccountsThroughUseCaseAndMapper() = runTest {
        val viewModel = viewModel { page -> AccountPage(listOf(account("one")), page, 1) }
        advanceUntilIdle()

        assertTrue(viewModel.state.value.hasLoaded)
        assertEquals("one", viewModel.state.value.accounts.single().id)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.nextPage)
    }

    @Test
    fun successfulEmptyPageIsNotAnInitialOrErrorState() = runTest {
        val viewModel = viewModel { page -> AccountPage(emptyList(), page, 0) }
        advanceUntilIdle()

        assertTrue(viewModel.state.value.hasLoaded)
        assertTrue(viewModel.state.value.accounts.isEmpty())
        assertNull(viewModel.state.value.error)
    }

    @Test
    fun preservesAccountsWhenRefreshFails() = runTest {
        var fail = false
        val viewModel = viewModel { page ->
            if (fail) throw AccountLoadException(AccountFailure.NETWORK)
            AccountPage(listOf(account("one")), page, 1)
        }
        advanceUntilIdle()
        val loaded = viewModel.state.value.accounts
        fail = true

        viewModel.onAction(AccountListAction.Refresh)
        advanceUntilIdle()

        assertEquals(loaded, viewModel.state.value.accounts)
        assertEquals(AccountListError.Network, viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun appendsPagesWithoutDuplicatingAccountIdsAndStopsAtTheEnd() = runTest {
        val requests = mutableListOf<Int>()
        val viewModel = viewModel { page ->
            requests += page
            AccountPage(if (page == 1) listOf(account("one")) else listOf(account("one"), account("two")), page, 2)
        }
        advanceUntilIdle()

        viewModel.onAction(AccountListAction.LoadNextPage)
        advanceUntilIdle()
        viewModel.onAction(AccountListAction.LoadNextPage)
        advanceUntilIdle()

        assertEquals(listOf(1, 2), requests)
        assertEquals(listOf("one", "two"), viewModel.state.value.accounts.map { it.id })
        assertNull(viewModel.state.value.nextPage)
    }

    @Test
    fun failedNextPageRequiresExplicitRetryAndPreservesTheCursor() = runTest {
        var fail = true
        val requests = mutableListOf<Int>()
        val viewModel = viewModel { page ->
            requests += page
            if (page == 2 && fail) throw AccountLoadException(AccountFailure.SERVER)
            AccountPage(listOf(account("page-$page")), page, 2)
        }
        advanceUntilIdle()
        viewModel.onAction(AccountListAction.LoadNextPage)
        advanceUntilIdle()
        assertEquals(AccountListError.Server, viewModel.state.value.loadMoreError)
        assertEquals(2, viewModel.state.value.nextPage)

        viewModel.onAction(AccountListAction.LoadNextPage)
        advanceUntilIdle()
        assertEquals(listOf(1, 2), requests)

        fail = false
        viewModel.onAction(AccountListAction.RetryNextPage)
        advanceUntilIdle()

        assertEquals(listOf(1, 2, 2), requests)
        assertNull(viewModel.state.value.loadMoreError)
        assertEquals(2, viewModel.state.value.accounts.size)
    }

    @Test
    fun guardsConcurrentLoadsAndPropagatesCancellationWithoutAnError() = runTest {
        val response = CompletableDeferred<AccountPage>()
        var requests = 0
        val viewModel = viewModel {
            requests++
            response.await()
        }
        runCurrent()
        viewModel.onAction(AccountListAction.Refresh)
        viewModel.onAction(AccountListAction.LoadNextPage)
        runCurrent()
        assertEquals(1, requests)
        assertTrue(viewModel.state.value.isLoading)

        response.completeExceptionally(CancellationException())
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    private fun viewModel(load: suspend (Int) -> AccountPage): AccountListViewModel {
        val repository = object : AccountRepository {
            override suspend fun getAccounts(page: Int, limit: Int): AccountPage = load(page)
        }
        return AccountListViewModel(GetAccountsUseCase(repository), AccountListUiMapper())
    }

    private fun account(id: String) = Account(id, id, "asset", true, "10.00".toBigDecimal(), "USD")
}
