package william.miranda.brutusfly.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.model.TransactionGroup
import william.miranda.brutusfly.domain.model.TransactionPage
import william.miranda.brutusfly.domain.repository.TransactionRepository

class GetTransactionsUseCaseTest {
    @Test
    fun delegatesPaginationToRepository() = runTest {
        val expected = TransactionPage(listOf(TransactionGroup("test", null, emptyList())), 2, 3)
        var requestedPage = 0
        var requestedLimit = 0
        val repository = object : TransactionRepository {
            override suspend fun getTransactions(page: Int, limit: Int): TransactionPage {
                requestedPage = page
                requestedLimit = limit
                return expected
            }
        }

        val useCase: SuspendUseCase<GetTransactionsUseCase.Input, TransactionPage> =
            GetTransactionsUseCase(repository)
        val result = useCase(GetTransactionsUseCase.Input(page = 2, limit = 10))

        assertEquals(expected, result)
        assertEquals(2, requestedPage)
        assertEquals(10, requestedLimit)
    }

    @Test
    fun defaultParamsRequestFirstPageWithFiftyItems() = runTest {
        var requestedParams: GetTransactionsUseCase.Input? = null
        val repository = object : TransactionRepository {
            override suspend fun getTransactions(page: Int, limit: Int): TransactionPage {
                requestedParams = GetTransactionsUseCase.Input(page = page, limit = limit)
                return TransactionPage(emptyList(), page, 1)
            }
        }

        val result = GetTransactionsUseCase(repository)(GetTransactionsUseCase.Input())

        assertEquals(TransactionPage(emptyList(), 1, 1), result)
        assertEquals(GetTransactionsUseCase.Input(page = 1, limit = 50), requestedParams)
    }
}
