package it.airgap.beaconsdk.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import it.airgap.beaconsdk.internal.BeaconApp
import it.airgap.beaconsdk.internal.utils.logInfo

public class BeaconInitProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        context?.let {
            BeaconApp.create(it)
            logInfo(TAG, "BeaconApp created")
        } ?: run {
            logInfo(TAG, "BeaconApp could not be created")
        }

        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0

    internal companion object {
        const val TAG = "BeaconInitProvider"
    }
}