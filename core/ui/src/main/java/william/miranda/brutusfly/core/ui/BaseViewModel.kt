package william.miranda.brutusfly.core.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Shared MVI contract. Subclasses initialize their work explicitly, typically in init.
 * States must be immutable; reading state never triggers initialization or other work.
 */
abstract class BaseViewModel<STATE : Any, ACTION : Any, EVENT : Any>(
    initialState: STATE
) : ViewModel() {

    private val _state = MutableStateFlow(value = initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _events = Channel<EVENT>(capacity = Channel.BUFFERED)

    /**
     * Transient effects for a single UI collector. Multiple collectors divide events.
     * Buffered events can wait for a collector, but delivery is not durable or exactly-once.
     * Information that must survive collection cancellation belongs in state instead.
     */
    val events: Flow<EVENT> = _events.receiveAsFlow()

    abstract fun onAction(action: ACTION)

    /** Atomic transformation. Must be pure: it may run more than once under contention. */
    protected fun updateState(transform: STATE.() -> STATE) {
        _state.update { currentState -> currentState.transform() }
    }

    /** Checks the type against the current state inside the atomic update. */
    protected inline fun <reified T : STATE> updateIfState(
        crossinline transform: T.() -> STATE
    ) {
        updateState {
            if (this is T) transform() else this
        }
    }

    /** Snapshot only; do not use a captured state to perform read-modify-write updates. */
    protected inline fun <reified T : STATE> getCurrentStateAs(): T? = state.value as? T

    /**
     * Call from a coroutine owned by the subclass, normally viewModelScope.
     * Suspends if the buffer is full; does not start a detached coroutine or drop events.
     */
    protected suspend fun emitEvent(event: EVENT) {
        _events.send(
            element = event
        )
    }
}
