package william.miranda.brutusfly.core.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BaseViewModelTest {
    @Test
    fun exposesInitialState() {
        val viewModel = TestViewModel()

        assertEquals(TestState.Count(), viewModel.state.value)
    }

    @Test
    fun updatesStateWithoutCollectors() {
        val viewModel = TestViewModel()

        viewModel.onAction(TestAction.Increment)

        assertEquals(TestState.Count(1), viewModel.state.value)
        assertEquals(TestState.Count(1), viewModel.currentCount())
    }

    @Test
    fun conditionalUpdateIgnoresOtherStateTypes() {
        val viewModel = TestViewModel()

        viewModel.onAction(TestAction.Reset)
        viewModel.onAction(TestAction.Increment)

        assertEquals(TestState.Idle, viewModel.state.value)
        assertNull(viewModel.currentCount())
    }

    @Test
    fun concurrentUpdatesDoNotLoseChanges() = runBlocking {
        val viewModel = TestViewModel()

        val jobs = List(4) {
            launch(Dispatchers.Default) {
                repeat(1_000) { viewModel.onAction(TestAction.Increment) }
            }
        }
        jobs.forEach { it.join() }

        assertEquals(TestState.Count(4_000), viewModel.state.value)
    }

    @Test
    fun buffersEventsUntilCollectionStarts() = runBlocking {
        val viewModel = TestViewModel()

        viewModel.notifyUser()

        assertEquals(TestEvent.Notified, viewModel.events.first())
    }

    private sealed interface TestState {
        data class Count(val value: Int = 0) : TestState
        data object Idle : TestState
    }

    private sealed interface TestAction {
        data object Increment : TestAction
        data object Reset : TestAction
    }

    private sealed interface TestEvent {
        data object Notified : TestEvent
    }

    private class TestViewModel : BaseViewModel<TestState, TestAction, TestEvent>(TestState.Count()) {
        override fun onAction(action: TestAction) {
            when (action) {
                TestAction.Increment -> updateIfState<TestState.Count> { copy(value = value + 1) }
                TestAction.Reset -> updateState { TestState.Idle }
            }
        }

        fun currentCount(): TestState.Count? = getCurrentStateAs<TestState.Count>()

        suspend fun notifyUser() {
            emitEvent(TestEvent.Notified)
        }
    }
}
