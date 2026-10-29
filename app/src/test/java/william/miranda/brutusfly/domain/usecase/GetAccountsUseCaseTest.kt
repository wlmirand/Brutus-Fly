package william.miranda.brutusfly.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import william.miranda.brutusfly.core.common.usecase.SuspendUseCase
import william.miranda.brutusfly.domain.model.AccountPage
import william.miranda.brutusfly.domain.repository.AccountRepository

class GetAccountsUseCaseTest {
    @Test
    fun delegatesPaginationAndReturnsTheDomainPage() = runTest {
        val expected = AccountPage(emptyList(), 2, 3)
        var requested: GetAccountsUseCase.Input? = null
        val repository = object : AccountRepository {
            override suspend fun getAccounts(page: Int, limit: Int): AccountPage {
                requested = GetAccountsUseCase.Input(page, limit)
                return expected
            }
        }
        val useCase: SuspendUseCase<GetAccountsUseCase.Input, AccountPage> = GetAccountsUseCase(repository)

        assertEquals(expected, useCase(GetAccountsUseCase.Input(page = 2, limit = 10)))
        assertEquals(GetAccountsUseCase.Input(2, 10), requested)
        assertEquals(GetAccountsUseCase.Input(1, 50), GetAccountsUseCase.Input())
    }
}
