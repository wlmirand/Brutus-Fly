package william.miranda.brutusfly.data.remote.dto.serverconfiguration

import kotlinx.serialization.Serializable

/** JSON response envelope returned by the server's about endpoint. */
@Serializable
data class AboutResponse(
    val data: AboutData
)

/** Server metadata required by setup; the non-optional version must be present in the response. */
@Serializable
data class AboutData(
    val version: String
)
