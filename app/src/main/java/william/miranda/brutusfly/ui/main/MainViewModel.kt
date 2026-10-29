package william.miranda.brutusfly.ui.main

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import william.miranda.brutusfly.core.ui.BaseViewModel
import william.miranda.brutusfly.domain.usecase.serverconfiguration.ClearServerConfigurationUseCase
import william.miranda.brutusfly.domain.usecase.serverconfiguration.ObserveServerConfigurationUseCase
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val observeConfiguration: ObserveServerConfigurationUseCase,
    private val clearConfiguration: ClearServerConfigurationUseCase
) : BaseViewModel<MainState, MainAction, Nothing>(
    initialState = MainState.Loading
) {
    private var configurationJob: Job? = null

    init {
        observe()
    }

    override fun onAction(
        action: MainAction
    ) {
        when (action) {
            MainAction.Retry -> observe()
            MainAction.ResetConfiguration -> observe(
                reset = true
            )
        }
    }

    private fun observe(
        reset: Boolean = false
    ) {
        configurationJob?.cancel()
        updateState { MainState.Loading }
        configurationJob = viewModelScope.launch {
            try {
                if (reset) clearConfiguration(
                    input = Unit
                )
                observeConfiguration(
                    input = Unit
                ).collect { configured ->
                    updateState { if (configured) MainState.Ready else MainState.Setup }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                updateState { MainState.ConfigurationError }
            }
        }
    }
}
