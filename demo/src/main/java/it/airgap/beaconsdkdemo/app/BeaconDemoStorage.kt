package it.airgap.beaconsdkdemo.app

import android.content.SharedPreferences
import dagger.Reusable
import it.airgap.beaconsdkdemo.data.Account
import it.airgap.beaconsdkdemo.data.PrivateAccount
import it.airgap.beaconsdkdemo.utils.getSerializable
import it.airgap.beaconsdkdemo.utils.putSerializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

@Reusable
class BeaconDemoStorage @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val json: Json,
) {

    var accounts: List<Account>
        get() = with(json) { sharedPreferences.getSerializable(KEY_WALLET_ACCOUNTS, listOf()) }
        private set(value) {
            with(json) {
                sharedPreferences.putSerializable(KEY_WALLET_ACCOUNTS, value)
            }
        }

    fun saveAccount(account: PrivateAccount) {
        accounts = accounts + account.toPublic()
    }

    companion object {
        private const val KEY_WALLET_ACCOUNTS = "walletAccounts"
    }
}