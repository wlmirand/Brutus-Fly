package william.miranda.brutusfly.ui.setup

import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationFailure

internal data class SetupState(
    val baseUrl: String = "",
    val token: String = "",
    val isSaving: Boolean = false,
    val error: ConfigurationFailure? = null
) {
    override fun toString(): String = "SetupState(isSaving=$isSaving, error=$error)"
}

internal sealed interface SetupAction {
    data class UrlChanged(
        val value: String
    ) : SetupAction
    class TokenChanged(
        val value: String
    ) : SetupAction
    data object Save : SetupAction
}
