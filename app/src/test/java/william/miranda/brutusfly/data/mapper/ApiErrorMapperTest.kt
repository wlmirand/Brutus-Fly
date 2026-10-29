package william.miranda.brutusfly.data.mapper

import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import william.miranda.brutusfly.core.network.model.ApiError
import william.miranda.brutusfly.domain.model.ConfigurationFailure
import william.miranda.brutusfly.domain.model.TransactionFailure
import java.io.IOException

class ApiErrorMapperTest {
    private val configurationMapper = ConfigurationApiErrorMapper()
    private val transactionMapper = TransactionApiErrorMapper()

    @Test
    fun httpErrorsAreInterpretedInTheRepositoryContext() {
        assertEquals(ConfigurationFailure.UNAUTHORIZED, configurationMapper.map(http(401)).failure)
        assertEquals(TransactionFailure.UNAUTHORIZED, transactionMapper.map(http(403)).failure)
        assertEquals(ConfigurationFailure.SERVER, configurationMapper.map(http(503)).failure)
        assertEquals(TransactionFailure.SERVER, transactionMapper.map(http(500)).failure)
        assertEquals(ConfigurationFailure.INVALID_SERVER, configurationMapper.map(http(404)).failure)
        assertEquals(TransactionFailure.UNKNOWN, transactionMapper.map(http(404)).failure)
    }

    @Test
    fun networkErrorsPreserveTheOriginalCause() {
        val cause = IOException("offline")
        val error = ApiError.Network(cause)

        assertEquals(ConfigurationFailure.NETWORK, configurationMapper.map(error).failure)
        assertEquals(TransactionFailure.NETWORK, transactionMapper.map(error).failure)
        assertSame(cause, transactionMapper.map(error).cause)
    }

    @Test
    fun serializationIsAnInvalidServerDuringSetupButUnknownForTransactions() {
        val error = ApiError.Serialization(SerializationException("Invalid JSON"))

        assertEquals(ConfigurationFailure.INVALID_SERVER, configurationMapper.map(error).failure)
        assertEquals(TransactionFailure.UNKNOWN, transactionMapper.map(error).failure)
    }

    private fun http(code: Int) = ApiError.Http(code, emptyMap(), null)
}
