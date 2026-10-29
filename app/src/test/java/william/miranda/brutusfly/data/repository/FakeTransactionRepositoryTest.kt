package william.miranda.brutusfly.data.repository

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeTransactionRepositoryTest {
    @Test
    fun paginationDoesNotRepeatOrSkipRecords() = runTest {
        val repository = FakeTransactionRepository()
        val all = repository.getTransactions(page = 1, limit = 50).groups
        val pages = (1..4).flatMap { repository.getTransactions(page = it, limit = 2).groups }

        assertEquals(all.map { it.id }, pages.map { it.id })
        assertTrue(repository.getTransactions(page = 5, limit = 2).groups.isEmpty())
        assertTrue(repository.getTransactions(page = Int.MAX_VALUE, limit = Int.MAX_VALUE).groups.isEmpty())
        assertEquals(2, repository.getTransactions(page = 1, limit = 2).nextPage)
        assertEquals(null, repository.getTransactions(page = 4, limit = 2).nextPage)
    }
}
