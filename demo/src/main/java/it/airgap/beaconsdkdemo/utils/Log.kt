package it.airgap.beaconsdkdemo.utils

import android.util.Log

fun logInfo(tag: String, message: String) {
    Log.i(tag, message.replaceFirstChar(Char::titlecase))
}

fun logDebug(tag: String, message: String) {
    Log.d(tag, message.replaceFirstChar(Char::titlecase))
}

fun logError(tag: String, error: Throwable) {
    Log.e(tag, error.message?.replaceFirstChar(Char::titlecase), error)
}