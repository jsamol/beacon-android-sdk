package it.airgap.beaconsdkdemo.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import it.airgap.tezos.core.Tezos
import javax.inject.Inject

@HiltAndroidApp
class BeaconDemoApp : Application() {
    @Inject
    lateinit var tezos: Tezos

    override fun onCreate() {
        super.onCreate()

        Tezos.setDefault(tezos)
    }
}