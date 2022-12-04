package it.airgap.beaconsdkdemo.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (a: T1, b: T2) -> R,
): StateFlow<R> = flow1.combine(flow2, transform).stateIn(scope, started, transform(flow1.value, flow2.value))