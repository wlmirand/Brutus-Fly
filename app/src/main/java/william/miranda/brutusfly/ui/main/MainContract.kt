package william.miranda.brutusfly.ui.main

internal sealed interface MainState {
    data object Loading : MainState
    data object Setup : MainState
    data object Ready : MainState
    data object ConfigurationError : MainState
}

internal sealed interface MainAction {
    data object Retry : MainAction
    data object ResetConfiguration : MainAction
}
