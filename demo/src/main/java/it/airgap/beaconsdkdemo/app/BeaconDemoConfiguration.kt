package it.airgap.beaconsdkdemo.app

import android.content.SharedPreferences
import dagger.Reusable
import javax.inject.Inject

@Reusable
class BeaconDemoConfiguration @Inject constructor(private val sharedPreferences: SharedPreferences) {

}