package william.miranda.brutusfly.ui.setup

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import william.miranda.brutusfly.core.ui.BaseViewModel
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationException
import william.miranda.brutusfly.domain.model.serverconfiguration.ConfigurationFailure
import william.miranda.brutusfly.domain.usecase.serverconfiguration.ConfigureServerUseCase
import javax.inject.Inject

@HiltViewModel
internal class SetupViewModel @Inject constructor(
    private val configureServer: ConfigureServerUseCase
) : BaseViewModel<SetupState, SetupAction, Nothing>(
    initialState = SetupState()
) {
    override fun onAction(
        action: SetupAction
    ) {
        if (state.value.isSaving) return
        when (action) {
            is SetupAction.UrlChanged -> updateState {
                copy(
                    baseUrl = action.value,
                    error = null
                )
            }
            is SetupAction.TokenChanged -> updateState {
                copy(
                    token = action.value,
                    error = null
                )
            }
            SetupAction.Save -> save()
        }
    }

    private fun save() {
        val input = state.value
        updateState {
            copy(
                isSaving = true,
                error = null
            )
        }
        viewModelScope.launch {
            try {
                configureServer(
                    input = ConfigureServerUseCase.Input(
                        baseUrl = input.baseUrl,
                        personalAccessToken = input.token
                    )
                )
                updateState {
                    copy(
                        token = "",
                        isSaving = false
                    )
                }
            } catch (cancelled: CancellationException) {
                updateState {
                    copy(
                        isSaving = false
                    )
                }
                throw cancelled
            } catch (exception: Exception) {
                val failure = (exception as? ConfigurationException)?.failure ?: ConfigurationFailure.UNKNOWN
                updateState {
                    copy(
                        isSaving = false,
                        error = failure
                    )
                }
            }
        }
    }
}
