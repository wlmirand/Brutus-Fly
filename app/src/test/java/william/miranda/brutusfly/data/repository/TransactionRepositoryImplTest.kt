package william.miranda.brutusfly.data.repository

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import william.miranda.brutusfly.core.common.mapper.MappingException
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.core.network.model.ApiResult
import william.miranda.brutusfly.data.mapper.TransactionApiErrorMapper
import william.miranda.brutusfly.data.mapper.TransactionGroupDtoMapper
import william.miranda.brutusfly.data.mapper.TransactionSplitDtoMapper
import william.miranda.brutusfly.data.mapper.TransactionPageDtoMapper
import william.miranda.brutusfly.data.remote.FireflyApi
import william.miranda.brutusfly.data.remote.dto.AboutResponse
import william.miranda.brutusfly.data.remote.dto.AccountsResponse
import william.miranda.brutusfly.data.remote.dto.TransactionGroupAttributesDto
import william.miranda.brutusfly.data.remote.dto.TransactionGroupDto
import william.miranda.brutusfly.data.remote.dto.TransactionSplitDto
import william.miranda.brutusfly.data.remote.dto.TransactionsResponse
import william.miranda.brutusfly.data.remote.dto.TransactionMetaDto
import william.miranda.brutusfly.data.remote.dto.TransactionPaginationDto
import william.miranda.brutusfly.domain.model.TransactionFailure
import william.miranda.brutusfly.domain.model.TransactionLoadException

class TransactionRepositoryImplTest {
    @Test
    fun successReturnsDomainModelsInsteadOfTransportWrappers() = runTest {
        val repository = repository { success("12.34") }

        val result = repository.getTransactions(page = 1, limit = 10)

        assertEquals("group", result.groups.single().id)
        assertEquals("12.34".toBigDecimal(), result.groups.single().splits.single().amount)
        assertEquals(2, result.nextPage)
    }

    @Test
    fun apiErrorBecomesADomainFailure() = runTest {
        val repository = repository { ApiResult.Error(ApiError.Http(401, emptyMap(), null)) }

        try {
            repository.getTransactions(1, 50)
            throw AssertionError("Expected domain failure")
        } catch (exception: TransactionLoadException) {
            assertEquals(TransactionFailure.UNAUTHORIZED, exception.failure)
        }
    }

    @Test
    fun dtoMappingFailureIsNotReportedAsATransportFailure() = runTest {
        val repository = repository { success("invalid") }

        try {
            repository.getTransactions(1, 50)
            throw AssertionError("Expected mapping failure")
        } catch (exception: TransactionLoadException) {
            assertEquals(TransactionFailure.UNKNOWN, exception.failure)
            assertTrue(exception.cause is MappingException)
        }
    }

    @Test
    fun cancellationPropagatesWithoutDomainWrapping() = runTest {
        val expected = CancellationException("Cancelled")
        val repository = repository { throw expected }

        try {
            repository.getTransactions(1, 50)
            throw AssertionError("Expected cancellation")
        } catch (exception: CancellationException) {
            assertSame(expected, exception)
        }
    }

    @Test
    fun responseForADifferentPageIsRejectedToPreventPaginationLoops() = runTest {
        val repository = repository { success("12.34") }
        try {
            repository.getTransactions(page = 2, limit = 10)
            throw AssertionError("Expected mismatched page to fail")
        } catch (exception: TransactionLoadException) {
            assertTrue(exception.cause is MappingException)
        }
    }

    private fun repository(load: suspend () -> ApiResult<TransactionsResponse>): TransactionRepositoryImpl {
        val api = object : FireflyApi {
            override suspend fun getAccounts(page: Int, limit: Int): ApiResult<AccountsResponse> =
                error("Not used by transaction repository")
            override suspend fun getAbout(): ApiResult<AboutResponse> = error("Not used by transaction repository")
            override suspend fun getTransactions(page: Int, limit: Int): ApiResult<TransactionsResponse> = load()
        }
        return TransactionRepositoryImpl(
            api,
            TransactionPageDtoMapper(TransactionGroupDtoMapper(TransactionSplitDtoMapper())),
            TransactionApiErrorMapper(),
        )
    }

    private fun success(amount: String) = ApiResult.Success(
        data = TransactionsResponse(
            meta = TransactionMetaDto(TransactionPaginationDto(currentPage = 1, totalPages = 2)),
            data = listOf(
                TransactionGroupDto(
                    id = "group",
                    attributes = TransactionGroupAttributesDto(
                        transactions = listOf(
                            TransactionSplitDto("withdrawal", "2026-10-25T10:00:00Z", amount, "description"),
                        ),
                    ),
                ),
            ),
        ),
        responseCode = 200,
        headers = emptyMap(),
    )
}
