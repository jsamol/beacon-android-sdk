package it.airgap.beaconsdkdemo.utils

import android.content.SharedPreferences
import kotlinx.serialization.Contextual
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun SharedPreferences.putString(key: String, value: String?) {
    edit().putString(key, value).apply()
}

context(Json)
inline fun <reified T> SharedPreferences.getSerializable(key: String, default: @Contextual T): T {
    val encoded = getString(key, null)

    return encoded?.let { decodeFromString(encoded) } ?: default
}

context(Json)
inline fun <reified T> SharedPreferences.putSerializable(key: String, value: @Contextual T) {
    val encoded = encodeToString(value)
    putString(key, encoded)
}