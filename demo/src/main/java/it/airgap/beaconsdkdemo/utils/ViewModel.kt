package it.airgap.beaconsdkdemo.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

fun <T1, T2, R> ViewModel.combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    started: SharingStarted = SharingStarted.Eagerly,
    transform: (a: T1, b: T2) -> R,
): StateFlow<R> = combineState(flow1, flow2, viewModelScope, started, transform)