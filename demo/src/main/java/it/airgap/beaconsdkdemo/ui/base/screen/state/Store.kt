package it.airgap.beaconsdkdemo.ui.base.screen.state

import it.airgap.beaconsdkdemo.utils.logDebug
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.Closeable

@OptIn(FlowPreview::class)
abstract class Store<VS : ViewState, A : Action, AR : ActionResult, D : Dispatcher<A, AR>, R : Reducer<VS, AR>>(
    initViewState: VS,
    private val dispatcher: D,
    private val reducer: R,
) : Closeable {
    private val actionScope: CoroutineScope = CoroutineScope(CoroutineName("MviStore\$actionScope") + SupervisorJob())

    private val actionSharedFlow: MutableSharedFlow<A> = MutableSharedFlow(extraBufferCapacity = SIZE_BUFFER_ACTION)

    private var _viewStateFlow: MutableStateFlow<VS> = MutableStateFlow(initViewState)
    val viewStateFlow: StateFlow<VS> by lazy { _viewStateFlow.asStateFlow() }

    init {
        actionSharedFlow
            .eachLogDebug("Action")
            .flatMapConcat { dispatcher.dispatch(it) }
            .eachLogDebug("Action Result")
            .scan(_viewStateFlow.value) { viewState, actionResult -> reducer.reduce(viewState, actionResult) }
            .eachLogDebug("View State")
            .debounce(TIME_DEBOUNCE_ACTION)
            .conflate()
            .onEach { _viewStateFlow.emit(it) }
            .launchIn(actionScope)
    }

    suspend fun intent(action: A) {
        with(actionSharedFlow) {
            awaitSubscription()
            emit(action)
        }
    }

    override fun close() {
        actionScope.coroutineContext.cancel()
    }

    private suspend fun <T> MutableSharedFlow<T>.awaitSubscription() = coroutineScope {
        subscriptionCount.dropWhile { it == 0 }.take(1).launchIn(this)
    }

    private fun <T> Flow<T>.eachLogDebug(name: String): Flow<T> =
        onEach { logDebug(this@Store.toString(), "name: $it")}

    companion object {
        private const val SIZE_BUFFER_ACTION = 64
        private const val TIME_DEBOUNCE_ACTION = 250L
    }
}