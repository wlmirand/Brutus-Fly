package william.miranda.brutusfly.ui.transactionlist

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import william.miranda.brutusfly.ui.transactionlist.model.TransactionFilter
import william.miranda.brutusfly.ui.transactionlist.model.TransactionPeriod
import william.miranda.brutusfly.ui.transactionlist.model.sampleTransactionGroups

class TransactionListStateTest {
    @Test
    fun defaultStateHasNotLoadedAndHasNoError() {
        val state = TransactionListState()

        assertFalse(state.hasLoaded)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertTrue(state.groups.isEmpty())
    }

    @Test
    fun successfulEmptyResultDiffersFromNotLoaded() {
        val state = TransactionListState(hasLoaded = true)

        assertTrue(state.hasLoaded)
        assertTrue(state.groups.isEmpty())
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun initialLoadFailureDoesNotMarkDataAsLoaded() {
        val loading = TransactionListState(isLoading = true)

        val failed = loading.copy(isLoading = false, error = TransactionListError.Network)

        assertFalse(failed.hasLoaded)
        assertFalse(failed.isLoading)
        assertEquals(TransactionListError.Network, failed.error)
        assertTrue(failed.groups.isEmpty())
    }

    @Test
    fun failedRefreshPreservesDataAndSelections() {
        val loaded = TransactionListState(
            groups = sampleTransactionGroups,
            query = "grocery",
            selectedFilter = TransactionFilter.EXPENSE,
            selectedPeriod = TransactionPeriod.THIS_MONTH,
            hasLoaded = true,
        )

        val refreshing = loaded.copy(isLoading = true, error = null)
        assertTrue(refreshing.isLoading)
        assertEquals(loaded.groups, refreshing.groups)

        val failed = refreshing.copy(isLoading = false, error = TransactionListError.Network)

        assertTrue(failed.hasLoaded)
        assertFalse(failed.isLoading)
        assertEquals(TransactionListError.Network, failed.error)
        assertEquals(loaded.groups, failed.groups)
        assertEquals(loaded.query, failed.query)
        assertEquals(loaded.selectedFilter, failed.selectedFilter)
        assertEquals(loaded.selectedPeriod, failed.selectedPeriod)
        assertEquals(loaded.searchResults, failed.searchResults)
    }
}
