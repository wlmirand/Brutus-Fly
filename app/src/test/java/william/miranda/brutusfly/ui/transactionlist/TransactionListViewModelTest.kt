package william.miranda.brutusfly.ui.transactionlist

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CancellationException
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
import william.miranda.brutusfly.data.repository.FakeTransactionRepository
import william.miranda.brutusfly.domain.model.TransactionFailure
import william.miranda.brutusfly.domain.model.TransactionGroup
import william.miranda.brutusfly.domain.model.TransactionLoadException
import william.miranda.brutusfly.domain.model.TransactionPage
import william.miranda.brutusfly.domain.model.TransactionSplit
import java.time.LocalDate
import william.miranda.brutusfly.domain.repository.TransactionRepository
import william.miranda.brutusfly.domain.usecase.GetTransactionsUseCase
import william.miranda.brutusfly.ui.transactionlist.mapper.TransactionListUiMapper
import william.miranda.brutusfly.ui.transactionlist.model.TransactionFilter
import william.miranda.brutusfly.ui.transactionlist.model.TransactionKind

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionListViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadsFakeRepositoryThroughUseCase() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state.hasLoaded)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(7, state.groups.sumOf { it.transactions.size })
        state.groups.forEach { group ->
            assertEquals(group.transactions.sumOf { it.amountCents }, group.totalCents)
        }
    }

    @Test
    fun exposesLoadingBeforeFirstResponse() = runTest {
        val response = CompletableDeferred<TransactionPage>()
        val viewModel = createViewModel(repository { response.await() })
        runCurrent()

        assertTrue(viewModel.state.value.isLoading)
        assertFalse(viewModel.state.value.hasLoaded)

        response.complete(TransactionPage(emptyList(), 1, 1))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.hasLoaded)
        assertTrue(viewModel.state.value.groups.isEmpty())
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun searchAndFiltersAreAppliedToLoadedData() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onAction(TransactionListAction.SearchChanged("  SALARY  "))
        assertEquals(listOf("salary-0"), viewModel.state.value.searchResults.map { it.id })

        viewModel.onAction(TransactionListAction.SearchChanged(""))
        viewModel.onAction(TransactionListAction.FilterChanged(TransactionFilter.INCOME))

        val state = viewModel.state.value
        assertFalse(state.isSearching)
        assertEquals(listOf("salary-0"), state.searchResults.map { it.id })
        assertTrue(state.searchResults.all { it.kind == TransactionKind.INCOME })
    }

    @Test
    fun failedRefreshPreservesDataAndFiltersThenRetryRecovers() = runTest {
        val data = FakeTransactionRepository().getTransactions(1, 50)
        var shouldFail = false
        val viewModel = createViewModel(repository {
            if (shouldFail) throw TransactionLoadException(TransactionFailure.NETWORK)
            data
        })
        advanceUntilIdle()
        viewModel.onAction(TransactionListAction.SearchChanged("grocery"))
        viewModel.onAction(TransactionListAction.FilterChanged(TransactionFilter.EXPENSE))
        val loaded = viewModel.state.value

        shouldFail = true
        viewModel.onAction(TransactionListAction.Refresh)
        advanceUntilIdle()

        val failed = viewModel.state.value
        assertTrue(failed.hasLoaded)
        assertFalse(failed.isLoading)
        assertEquals(TransactionListError.Network, failed.error)
        assertEquals(loaded.groups, failed.groups)
        assertEquals(loaded.query, failed.query)
        assertEquals(loaded.selectedFilter, failed.selectedFilter)
        assertEquals(loaded.selectedPeriod, failed.selectedPeriod)

        shouldFail = false
        viewModel.onAction(TransactionListAction.Retry)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(loaded.searchResults, viewModel.state.value.searchResults)
    }

    @Test
    fun initialFailureCanBeDismissedWithoutMarkingDataAsLoaded() = runTest {
        val viewModel = createViewModel(repository {
            throw TransactionLoadException(TransactionFailure.UNAUTHORIZED)
        })
        advanceUntilIdle()

        assertEquals(TransactionListError.Unauthorized, viewModel.state.value.error)
        assertFalse(viewModel.state.value.hasLoaded)
        assertFalse(viewModel.state.value.isLoading)

        viewModel.onAction(TransactionListAction.ErrorDismissed)

        assertNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.hasLoaded)
    }

    @Test
    fun ignoresRefreshAndRetryWhileRequestIsRunning() = runTest {
        val response = CompletableDeferred<TransactionPage>()
        var calls = 0
        val viewModel = createViewModel(repository {
            calls++
            response.await()
        })
        runCurrent()

        viewModel.onAction(TransactionListAction.Refresh)
        viewModel.onAction(TransactionListAction.Retry)
        viewModel.onAction(TransactionListAction.LoadNextPage)
        viewModel.onAction(TransactionListAction.RetryNextPage)
        runCurrent()

        assertEquals(1, calls)
        response.complete(TransactionPage(emptyList(), 1, 1))
        advanceUntilIdle()
    }

    @Test
    fun cancellationIsNotConvertedIntoAnError() = runTest {
        val viewModel = createViewModel(repository { throw CancellationException() })
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertFalse(viewModel.state.value.hasLoaded)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun nextPageMergesDailyGroupsAndDeduplicatesOverlappingTransactionIds() = runTest {
        val requests = mutableListOf<Int>()
        val viewModel = createViewModel(pagedRepository { page ->
            requests += page
            when (page) {
                1 -> TransactionPage(listOf(transaction("one", "10.00")), 1, 2)
                else -> TransactionPage(listOf(transaction("one", "10.00"), transaction("two", "20.00")), 2, 2)
            }
        })
        advanceUntilIdle()
        assertEquals(2, viewModel.state.value.nextPage)

        viewModel.onAction(TransactionListAction.LoadNextPage)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf(1, 2), requests)
        assertEquals(2, state.groups.single().transactions.size)
        assertEquals(-3000L, state.groups.single().totalCents)
        assertNull(state.nextPage)
        assertFalse(state.isLoadingMore)

        viewModel.onAction(TransactionListAction.LoadNextPage)
        advanceUntilIdle()
        assertEquals(listOf(1, 2), requests)
    }

    @Test
    fun failedNextPagePreservesContentAndRequiresExplicitRetryOfTheSamePage() = runTest {
        val requests = mutableListOf<Int>()
        var fail = true
        val viewModel = createViewModel(pagedRepository { page ->
            requests += page
            if (page == 2 && fail) throw TransactionLoadException(TransactionFailure.NETWORK)
            TransactionPage(listOf(transaction("page-$page", "10.00")), page, 2)
        })
        advanceUntilIdle()
        val loaded = viewModel.state.value.groups

        viewModel.onAction(TransactionListAction.LoadNextPage)
        advanceUntilIdle()

        assertEquals(loaded, viewModel.state.value.groups)
        assertEquals(TransactionListError.Network, viewModel.state.value.loadMoreError)
        assertNull(viewModel.state.value.error)
        assertEquals(2, viewModel.state.value.nextPage)

        viewModel.onAction(TransactionListAction.LoadNextPage)
        advanceUntilIdle()
        assertEquals(listOf(1, 2), requests)

        fail = false
        viewModel.onAction(TransactionListAction.RetryNextPage)
        advanceUntilIdle()

        assertEquals(listOf(1, 2, 2), requests)
        assertNull(viewModel.state.value.loadMoreError)
        assertNull(viewModel.state.value.nextPage)
        assertEquals(2, viewModel.state.value.groups.single().transactions.size)
    }

    @Test
    fun repeatedScrollEventsDoNotStartConcurrentPageRequests() = runTest {
        val response = CompletableDeferred<TransactionPage>()
        val requests = mutableListOf<Int>()
        val viewModel = createViewModel(pagedRepository { page ->
            requests += page
            if (page == 1) TransactionPage(listOf(transaction("one", "10.00")), 1, 2)
            else response.await()
        })
        advanceUntilIdle()

        viewModel.onAction(TransactionListAction.LoadNextPage)
        runCurrent()
        assertTrue(viewModel.state.value.isLoadingMore)
        assertFalse(viewModel.state.value.isLoading)

        repeat(5) { viewModel.onAction(TransactionListAction.LoadNextPage) }
        viewModel.onAction(TransactionListAction.RetryNextPage)
        viewModel.onAction(TransactionListAction.Refresh)
        runCurrent()

        assertEquals(listOf(1, 2), requests)
        response.complete(TransactionPage(listOf(transaction("two", "20.00")), 2, 2))
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoadingMore)
    }

    @Test
    fun refreshReplacesAllAccumulatedPagesAndRestartsPagination() = runTest {
        var refreshed = false
        val requests = mutableListOf<Int>()
        val viewModel = createViewModel(pagedRepository { page ->
            requests += page
            val id = if (refreshed) "refreshed" else "page-$page"
            TransactionPage(listOf(transaction(id, "10.00")), page, 2)
        })
        advanceUntilIdle()
        viewModel.onAction(TransactionListAction.LoadNextPage)
        advanceUntilIdle()
        viewModel.onAction(TransactionListAction.SearchChanged("retained-query"))

        refreshed = true
        viewModel.onAction(TransactionListAction.Refresh)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(listOf(1, 2, 1), requests)
        assertEquals(listOf("refreshed-0"), state.groups.single().transactions.map { it.id })
        assertEquals("retained-query", state.query)
        assertEquals(2, state.nextPage)
    }

    @Test
    fun emptyFirstPageStopsPagination() = runTest {
        var requests = 0
        val viewModel = createViewModel(pagedRepository { page ->
            requests++
            TransactionPage(emptyList(), page, 0)
        })
        advanceUntilIdle()

        viewModel.onAction(TransactionListAction.LoadNextPage)
        advanceUntilIdle()

        assertEquals(1, requests)
        assertTrue(viewModel.state.value.hasLoaded)
        assertNull(viewModel.state.value.nextPage)
    }

    private fun transaction(id: String, amount: String) = TransactionGroup(
        id = id,
        groupTitle = null,
        splits = listOf(
            TransactionSplit(
                type = "withdrawal",
                date = "${LocalDate.now()}T10:00:00Z",
                amount = amount.toBigDecimal(),
                description = id,
                currencyCode = "BRL",
                sourceName = "account",
                destinationName = "payee",
            ),
        ),
    )

    private fun pagedRepository(load: suspend (Int) -> TransactionPage) = object : TransactionRepository {
        override suspend fun getTransactions(page: Int, limit: Int): TransactionPage = load(page)
    }

    private fun createViewModel(repository: TransactionRepository = FakeTransactionRepository()) =
        TransactionListViewModel(GetTransactionsUseCase(repository), TransactionListUiMapper())

    private fun repository(load: suspend () -> TransactionPage) = object : TransactionRepository {
        override suspend fun getTransactions(page: Int, limit: Int): TransactionPage = load()
    }
}
